package com.gurizadadointerior.clih.progress;

import com.gurizadadointerior.clih.rewards.PointTransaction;
import com.gurizadadointerior.clih.rewards.PointTransactionRepository;
import com.gurizadadointerior.clih.rewards.RewardPolicy;
import com.gurizadadointerior.clih.routines.HabitRepository;
import com.gurizadadointerior.clih.shared.ResourceNotFoundException;
import com.gurizadadointerior.clih.users.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class HabitCompletionService {

    static final UUID DEMO_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final HabitRepository habitRepository;
    private final HabitCompletionRepository completionRepository;
    private final UserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final RewardPolicy rewardPolicy;

    public HabitCompletionService(
            HabitRepository habitRepository,
            HabitCompletionRepository completionRepository,
            UserRepository userRepository,
            PointTransactionRepository pointTransactionRepository,
            RewardPolicy rewardPolicy
    ) {
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
        this.userRepository = userRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.rewardPolicy = rewardPolicy;
    }

    @Transactional
    public CompletionResponse complete(UUID habitId, String idempotencyKey, CompletionRequest request) {
        var existing = completionRepository.findByUserIdAndIdempotencyKey(DEMO_USER_ID, idempotencyKey);
        if (existing.isPresent()) {
            var user = userRepository.findById(DEMO_USER_ID)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário de demonstração não encontrado."));
            return new CompletionResponse(existing.get().getId(), 0, user.getXp(), true);
        }

        var habit = habitRepository.findById(habitId)
                .filter(item -> item.getUserId().equals(DEMO_USER_ID))
                .orElseThrow(() -> new ResourceNotFoundException("Atividade não encontrada."));
        var user = userRepository.findById(DEMO_USER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário de demonstração não encontrado."));

        var completion = completionRepository.save(new HabitCompletion(
                DEMO_USER_ID,
                habit,
                idempotencyKey,
                request.completedAt()
        ));
        var points = rewardPolicy.pointsFor(habit);
        var totalXp = user.addXp(points);
        pointTransactionRepository.save(new PointTransaction(DEMO_USER_ID, completion, points, totalXp));

        return new CompletionResponse(completion.getId(), points, totalXp, false);
    }
}

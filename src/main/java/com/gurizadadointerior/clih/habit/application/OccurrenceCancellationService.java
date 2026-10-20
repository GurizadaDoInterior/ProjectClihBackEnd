package com.gurizadadointerior.clih.habit.application;

import java.time.LocalDate;
import java.util.UUID;
import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import com.gurizadadointerior.clih.shared.error.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OccurrenceCancellationService {
    private final HabitApplicationService habits;
    private final HabitCompletionRepository completions;
    private final RewardApplicationService rewards;
    public OccurrenceCancellationService(HabitApplicationService habits, HabitCompletionRepository completions, RewardApplicationService rewards) {
        this.habits=habits; this.completions=completions; this.rewards=rewards;
    }
    @Transactional
    public void setCanceled(AppUser user, UUID habitId, LocalDate date, DayPeriod period, boolean canceled) {
        rewards.lockProgress(user.getId());
        habits.getOwnedActive(user.getId(), habitId);
        if (canceled && completions.existsByUserIdAndHabitIdAndActivityDate(user.getId(), habitId, date, period))
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Desfaça a conclusão antes de cancelar este dia.");
        habits.setOccurrenceCanceled(user, habitId, date, period, canceled);
    }
}

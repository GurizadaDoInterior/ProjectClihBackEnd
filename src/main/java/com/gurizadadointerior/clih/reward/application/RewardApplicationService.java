package com.gurizadadointerior.clih.reward.application;

import java.util.UUID;

import com.gurizadadointerior.clih.habit.domain.Habit;
import com.gurizadadointerior.clih.reward.domain.LevelPolicy;
import com.gurizadadointerior.clih.reward.domain.ProgressSnapshot;
import com.gurizadadointerior.clih.reward.domain.RewardPolicy;
import com.gurizadadointerior.clih.reward.domain.UserProgress;
import com.gurizadadointerior.clih.reward.domain.UserProgressRepository;
import com.gurizadadointerior.clih.reward.domain.XpReason;
import com.gurizadadointerior.clih.reward.domain.XpTransaction;
import com.gurizadadointerior.clih.reward.domain.XpTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RewardApplicationService {

	private final UserProgressRepository progressRepository;
	private final XpTransactionRepository transactionRepository;
	private final RewardPolicy rewardPolicy;
	private final LevelPolicy levelPolicy;

	public RewardApplicationService(
		UserProgressRepository progressRepository,
		XpTransactionRepository transactionRepository,
		RewardPolicy rewardPolicy,
		LevelPolicy levelPolicy
	) {
		this.progressRepository = progressRepository;
		this.transactionRepository = transactionRepository;
		this.rewardPolicy = rewardPolicy;
		this.levelPolicy = levelPolicy;
	}

	@Transactional
	public RewardResult awardHabitCompletion(UUID userId, Habit habit, UUID completionId, ProgressSnapshot streak) {
		progressRepository.createIfAbsent(userId);
		var progress = progressRepository.findByIdForUpdate(userId)
			.orElseThrow(() -> new IllegalStateException("User progress could not be loaded"));
		var previousLevel = progress.getCurrentLevel();
		var xp = rewardPolicy.experienceFor(habit);

		if (!transactionRepository.existsBySourceId(completionId)) {
			var nextTotalXp = progress.getTotalXp() + xp;
			progress.applyExperience(xp, levelPolicy.levelFor(nextTotalXp), streak);
			progressRepository.save(progress);
			transactionRepository.save(new XpTransaction(
				userId, xp, XpReason.HABIT_COMPLETION, completionId, nextTotalXp,
				"Conclusão do hábito " + habit.getName()
			));
		}

		return new RewardResult(xp, progress.getCurrentLevel() > previousLevel, toResponse(progress));
	}

	@Transactional(readOnly = true)
	public ProgressView getProgress(UUID userId) {
		return progressRepository.findById(userId)
			.map(this::toResponse)
			.orElse(new ProgressView(0, 1, 0, levelPolicy.xpForLevel(2), 0, 0, null));
	}

	private ProgressView toResponse(UserProgress progress) {
		return new ProgressView(
			progress.getTotalXp(),
			progress.getCurrentLevel(),
			levelPolicy.xpForLevel(progress.getCurrentLevel()),
			levelPolicy.xpForLevel(progress.getCurrentLevel() + 1),
			progress.getCurrentStreak(),
			progress.getLongestStreak(),
			progress.getLastActiveDate()
		);
	}

	public record RewardResult(int xpAwarded, boolean levelUp, ProgressView progress) {
	}
}

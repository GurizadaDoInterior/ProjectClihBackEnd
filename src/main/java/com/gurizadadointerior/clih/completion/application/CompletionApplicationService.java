package com.gurizadadointerior.clih.completion.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import com.gurizadadointerior.clih.completion.domain.HabitCompletion;
import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import com.gurizadadointerior.clih.habit.application.HabitApplicationService;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import com.gurizadadointerior.clih.reward.domain.StreakCalculator;
import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompletionApplicationService {

	private static final Duration MAXIMUM_CLOCK_SKEW = Duration.ofMinutes(5);
	private static final Duration MAXIMUM_OFFLINE_PERIOD = Duration.ofDays(14);

	private final HabitCompletionRepository repository;
	private final HabitApplicationService habitService;
	private final RewardApplicationService rewardService;
	private final StreakCalculator streakCalculator;
	private final Clock clock;

	public CompletionApplicationService(
		HabitCompletionRepository repository,
		HabitApplicationService habitService,
		RewardApplicationService rewardService,
		StreakCalculator streakCalculator,
		Clock clock
	) {
		this.repository = repository;
		this.habitService = habitService;
		this.rewardService = rewardService;
		this.streakCalculator = streakCalculator;
		this.clock = clock;
	}

	@Transactional
	public CompletionResult complete(
		AppUser user,
		UUID habitId,
		String idempotencyKey,
		CompleteHabitCommand command
	) {
		var normalizedKey = idempotencyKey.trim();
		var existing = repository.findByUserIdAndIdempotencyKey(user.getId(), normalizedKey);
		if (existing.isPresent()) {
			if (!existing.get().getHabitId().equals(habitId)) {
				throw new ApiException(ErrorCode.IDEMPOTENCY_KEY_REUSED);
			}
			return CompletionResult.replay(existing.get(), rewardService.getProgress(user.getId()));
		}

		var habit = habitService.getOwnedActive(user.getId(), habitId);
		var now = Instant.now(clock);
		var completedAt = command.completedAt() == null ? now : command.completedAt();
		validateCompletionTime(completedAt, now);

		var activityDate = completedAt.atZone(user.zoneId()).toLocalDate();
		var today = LocalDate.now(clock.withZone(user.zoneId()));
		if (activityDate.isAfter(today)) {
			throw new ApiException(ErrorCode.INVALID_COMPLETION_TIME);
		}
		if (!habit.isScheduledFor(activityDate)) {
			throw new ApiException(ErrorCode.HABIT_NOT_SCHEDULED, ErrorCode.HABIT_NOT_SCHEDULED.defaultMessage(),
				Map.of("activityDate", activityDate));
		}

		if (repository.existsByUserIdAndHabitIdAndActivityDate(user.getId(), habitId, activityDate)) {
			throw new ApiException(ErrorCode.HABIT_ALREADY_COMPLETED);
		}

		var achievedValue = command.achievedValue() == null ? habit.getTargetValue() : command.achievedValue();
		if (achievedValue.compareTo(habit.getTargetValue()) < 0) {
			throw new ApiException(ErrorCode.TARGET_NOT_REACHED, ErrorCode.TARGET_NOT_REACHED.defaultMessage(),
				Map.of("targetValue", habit.getTargetValue(), "achievedValue", achievedValue));
		}

		var completion = repository.saveAndFlush(new HabitCompletion(
			user.getId(), habitId, activityDate, completedAt, achievedValue, normalizedKey,
			command.note() == null ? null : command.note().trim()
		));

		var streak = streakCalculator.calculate(repository.findDistinctActivityDatesByUserId(user.getId()), today);
		var reward = rewardService.awardHabitCompletion(user.getId(), habit, completion.getId(), streak);
		completion.markXpAwarded(reward.xpAwarded());

		return CompletionResult.created(completion, reward.levelUp(), reward.progress());
	}

	private void validateCompletionTime(Instant completedAt, Instant now) {
		if (completedAt.isAfter(now.plus(MAXIMUM_CLOCK_SKEW)) || completedAt.isBefore(now.minus(MAXIMUM_OFFLINE_PERIOD))) {
			throw new ApiException(ErrorCode.INVALID_COMPLETION_TIME);
		}
	}
}

package com.gurizadadointerior.clih.completion.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.gurizadadointerior.clih.completion.domain.HabitCompletion;
import com.gurizadadointerior.clih.reward.application.ProgressView;

public record CompletionResult(
	UUID id,
	UUID habitId,
	LocalDate activityDate,
	Instant completedAt,
	BigDecimal achievedValue,
	int xpAwarded,
	boolean levelUp,
	boolean idempotentReplay,
	ProgressView progress
) {
	static CompletionResult created(HabitCompletion completion, boolean levelUp, ProgressView progress) {
		return from(completion, levelUp, false, progress);
	}

	static CompletionResult replay(HabitCompletion completion, ProgressView progress) {
		return from(completion, false, true, progress);
	}

	private static CompletionResult from(
		HabitCompletion completion,
		boolean levelUp,
		boolean replay,
		ProgressView progress
	) {
		return new CompletionResult(
			completion.getId(), completion.getHabitId(), completion.getActivityDate(),
			completion.getCompletedAt(), completion.getAchievedValue(), completion.getXpAwarded(),
			levelUp, replay, progress
		);
	}
}

package com.gurizadadointerior.clih.completion.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.gurizadadointerior.clih.completion.application.CompletionResult;
import com.gurizadadointerior.clih.reward.application.ProgressView;

public record CompletionResponse(
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
	public static CompletionResponse from(CompletionResult result) {
		return new CompletionResponse(
			result.id(), result.habitId(), result.activityDate(), result.completedAt(),
			result.achievedValue(), result.xpAwarded(), result.levelUp(),
			result.idempotentReplay(), result.progress()
		);
	}
}

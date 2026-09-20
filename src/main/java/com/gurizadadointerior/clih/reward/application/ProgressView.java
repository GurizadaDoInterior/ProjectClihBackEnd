package com.gurizadadointerior.clih.reward.application;

import java.time.LocalDate;

public record ProgressView(
	long totalXp,
	int level,
	long xpForCurrentLevel,
	long xpForNextLevel,
	int currentStreak,
	int longestStreak,
	LocalDate lastActiveDate
) {
}

package com.gurizadadointerior.clih.evolution.application.view;

public record EvolutionSummaryView(
	int activitiesCompletedToday,
	int activeDaysInMonth,
	int currentStreak,
	int longestStreak
) {
}

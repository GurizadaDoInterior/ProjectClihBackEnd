package com.gurizadadointerior.clih.evolution.application.view;

import java.time.LocalDate;

public record CalendarDayView(
	LocalDate date,
	EvolutionDayStatus status,
	int plannedActivities,
	int completedActivities,
	Integer completionPercentage,
	Integer sleepDurationMinutes,
	String sleepQuality,
	String sleepQualityLabel
) {
}

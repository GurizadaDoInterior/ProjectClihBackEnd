package com.gurizadadointerior.clih.today.application.view;

import java.time.LocalDate;

public record TodayWeekDayResponse(
	LocalDate date,
	String label,
	DayStatus status,
	boolean current,
	int planned,
	int completed,
	int progressPercentage
) {
}

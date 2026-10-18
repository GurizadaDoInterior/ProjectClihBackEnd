package com.gurizadadointerior.clih.evolution.application.view;

import java.time.LocalDate;
import java.util.List;

public record SleepComparisonView(
	LocalDate from,
	LocalDate to,
	Integer averageMinutes,
	int registeredDays,
	List<SleepDayView> days
) {
}

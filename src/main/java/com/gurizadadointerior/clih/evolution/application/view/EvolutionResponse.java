package com.gurizadadointerior.clih.evolution.application.view;

import java.time.LocalDate;
import java.util.List;

public record EvolutionResponse(
	String month,
	LocalDate selectedDate,
	EvolutionSummaryView summary,
	List<CalendarDayView> calendar,
	SelectedDayView selectedDay,
	SleepComparisonView sleepComparison,
	List<EvolutionAreaView> areas
) {
}

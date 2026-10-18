package com.gurizadadointerior.clih.evolution.application.view;

import java.time.LocalDate;
import java.util.List;

public record SelectedDayView(
	LocalDate date,
	SleepDayView sleep,
	List<SelectedActivityView> activities
) {
}

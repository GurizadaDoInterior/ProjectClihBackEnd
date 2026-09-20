package com.gurizadadointerior.clih.today.application.view;

import java.time.LocalDate;
import java.util.List;

import com.gurizadadointerior.clih.reward.application.ProgressView;

public record TodayResponse(
	LocalDate date,
	String displayName,
	ProgressView progress,
	TodaySummaryResponse summary,
	List<TodayWeekDayResponse> week,
	List<AreaProgressView> areaProgress,
	List<TodayHabitResponse> habits
) {
}

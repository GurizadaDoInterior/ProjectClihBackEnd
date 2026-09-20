package com.gurizadadointerior.clih.today.application.view;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.habit.domain.MeasurementType;

public record TodayHabitResponse(
	UUID id,
	String name,
	TodayAreaResponse area,
	MeasurementType measurementType,
	BigDecimal targetValue,
	String unit,
	DayPeriod dayPeriod,
	LocalTime preferredTime,
	boolean completed,
	UUID completionId,
	Instant completedAt,
	int xpAwarded
) {
}

package com.gurizadadointerior.clih.habit.application;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.habit.domain.MeasurementType;

public record CreateHabitCommand(
	UUID areaId,
	String name,
	String description,
	MeasurementType measurementType,
	BigDecimal targetValue,
	String unit,
	DayPeriod dayPeriod,
	LocalTime preferredTime,
	LocalDate startDate,
	LocalDate endDate,
	Set<DayOfWeek> scheduledDays
) {
}

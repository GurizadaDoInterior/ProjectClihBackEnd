package com.gurizadadointerior.clih.habit.api.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.habit.domain.Habit;
import com.gurizadadointerior.clih.habit.domain.MeasurementType;

public record HabitResponse(
	UUID id,
	UUID areaId,
	String name,
	String description,
	MeasurementType measurementType,
	BigDecimal targetValue,
	String unit,
	DayPeriod dayPeriod,
	LocalTime preferredTime,
	int position,
	LocalDate startDate,
	LocalDate endDate,
	Set<DayOfWeek> scheduledDays,
	boolean active,
	Instant createdAt
) {
	public static HabitResponse from(Habit habit) {
		return new HabitResponse(
			habit.getId(), habit.getAreaId(), habit.getName(), habit.getDescription(),
			habit.getMeasurementType(), habit.getTargetValue(), habit.getUnit(),
			habit.getDayPeriod(), habit.getPreferredTime(), habit.getPosition(), habit.getStartDate(),
			habit.getEndDate(), habit.getScheduledDays(), habit.isActive(), habit.getCreatedAt()
		);
	}
}

package com.gurizadadointerior.clih.evolution.application.view;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.gurizadadointerior.clih.habit.domain.DayPeriod;

public record SelectedActivityView(
	UUID habitId,
	String name,
	UUID areaId,
	String areaName,
	String areaColor,
	String areaIcon,
	Set<DayPeriod> dayPeriods,
	LocalTime preferredTime,
	boolean completed,
	UUID completionId,
	Instant completedAt
) {
}

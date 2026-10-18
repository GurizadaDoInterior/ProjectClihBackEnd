package com.gurizadadointerior.clih.evolution.application.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.gurizadadointerior.clih.sleep.domain.SleepRecord;

public record SleepDayView(
	LocalDate date,
	String sleptAt,
	String wokeAt,
	Integer durationMinutes,
	String quality,
	String qualityLabel
) {
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

	public static SleepDayView from(SleepRecord record) {
		return new SleepDayView(
			record.getDate(),
			record.getSleptAt().format(TIME_FORMAT),
			record.getWokeAt().format(TIME_FORMAT),
			record.durationMinutes(),
			record.getQuality().name(),
			record.getQuality().label()
		);
	}

	public static SleepDayView empty(LocalDate date) {
		return new SleepDayView(date, null, null, null, null, null);
	}
}

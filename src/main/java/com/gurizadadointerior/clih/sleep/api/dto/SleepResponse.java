package com.gurizadadointerior.clih.sleep.api.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.gurizadadointerior.clih.sleep.domain.SleepRecord;

public record SleepResponse(
	LocalDate date,
	String sleptAt,
	String wokeAt,
	String quality,
	String qualityLabel,
	int durationMinutes
) {
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

	public static SleepResponse from(SleepRecord record) {
		return new SleepResponse(
			record.getDate(),
			record.getSleptAt().format(TIME_FORMAT),
			record.getWokeAt().format(TIME_FORMAT),
			record.getQuality().name(),
			record.getQuality().label(),
			record.durationMinutes()
		);
	}
}

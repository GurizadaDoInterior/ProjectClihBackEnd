package com.gurizadadointerior.clih.sleep.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class SleepRecord {

	private final UUID userId;
	private final LocalDate date;
	private final LocalTime sleptAt;
	private final LocalTime wokeAt;
	private final SleepQuality quality;

	public SleepRecord(UUID userId, LocalDate date, LocalTime sleptAt, LocalTime wokeAt, SleepQuality quality) {
		this.userId = Objects.requireNonNull(userId, "userId");
		this.date = Objects.requireNonNull(date, "date");
		this.sleptAt = Objects.requireNonNull(sleptAt, "sleptAt");
		this.wokeAt = Objects.requireNonNull(wokeAt, "wokeAt");
		this.quality = Objects.requireNonNull(quality, "quality");

		if (sleptAt.equals(wokeAt)) {
			throw new IllegalArgumentException("Os horários de dormir e acordar devem ser diferentes.");
		}
	}

	public int durationMinutes() {
		var minutes = (int) ChronoUnit.MINUTES.between(sleptAt, wokeAt);
		return minutes < 0 ? minutes + 24 * 60 : minutes;
	}

	public UUID getUserId() {
		return userId;
	}

	public LocalDate getDate() {
		return date;
	}

	public LocalTime getSleptAt() {
		return sleptAt;
	}

	public LocalTime getWokeAt() {
		return wokeAt;
	}

	public SleepQuality getQuality() {
		return quality;
	}
}

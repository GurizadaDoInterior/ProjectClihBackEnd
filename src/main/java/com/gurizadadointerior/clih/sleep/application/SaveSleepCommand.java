package com.gurizadadointerior.clih.sleep.application;

import java.time.LocalDate;
import java.time.LocalTime;

import com.gurizadadointerior.clih.sleep.domain.SleepQuality;

public record SaveSleepCommand(
	LocalDate date,
	LocalTime sleptAt,
	LocalTime wokeAt,
	SleepQuality quality
) {
}

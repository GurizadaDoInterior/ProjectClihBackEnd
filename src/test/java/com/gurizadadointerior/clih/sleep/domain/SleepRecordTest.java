package com.gurizadadointerior.clih.sleep.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SleepRecordTest {

	@Test
	void calculatesDurationAcrossMidnight() {
		var record = record("23:30", "07:00", SleepQuality.GOOD);

		assertEquals(450, record.durationMinutes());
	}

	@Test
	void calculatesDurationWithinTheSameDay() {
		var record = record("01:00", "06:30", SleepQuality.FAIR);

		assertEquals(330, record.durationMinutes());
	}

	@Test
	void rejectsEqualSleepAndWakeTimes() {
		assertThrows(IllegalArgumentException.class, () -> record("07:00", "07:00", SleepQuality.GOOD));
	}

	@Test
	void acceptsStableCodesAndLegacyPortugueseLabels() {
		assertEquals(SleepQuality.GOOD, SleepQuality.fromExternalValue("GOOD"));
		assertEquals(SleepQuality.GOOD, SleepQuality.fromExternalValue("Boa"));
		assertEquals(SleepQuality.EXCELLENT, SleepQuality.fromExternalValue("Ótima"));
		assertEquals(SleepQuality.EXCELLENT, SleepQuality.fromExternalValue("otima"));
	}

	private SleepRecord record(String sleptAt, String wokeAt, SleepQuality quality) {
		return new SleepRecord(
			UUID.randomUUID(),
			LocalDate.of(2026, 10, 18),
			LocalTime.parse(sleptAt),
			LocalTime.parse(wokeAt),
			quality
		);
	}
}

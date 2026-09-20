package com.gurizadadointerior.clih.habit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class HabitTest {

	@Test
	void isScheduledOnlyOnConfiguredDaysInsideDateRange() {
		var habit = new Habit(
			UUID.randomUUID(), UUID.randomUUID(), "Estudar Java", null,
			MeasurementType.MINUTES, BigDecimal.valueOf(30), "minutos", DayPeriod.EVENING,
			null, 1, LocalDate.of(2026, 9, 1), null, Set.of(DayOfWeek.MONDAY)
		);

		assertThat(habit.isScheduledFor(LocalDate.of(2026, 9, 7))).isTrue();
		assertThat(habit.isScheduledFor(LocalDate.of(2026, 9, 8))).isFalse();
		assertThat(habit.isScheduledFor(LocalDate.of(2026, 8, 31))).isFalse();
	}
}

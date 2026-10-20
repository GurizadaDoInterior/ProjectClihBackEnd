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
    @Test
    void cancellationAffectsOnlyChosenDateAndPeriodAndCanBeRestored() {
        var day=LocalDate.of(2026,10,21);
        var habit=new Habit(UUID.randomUUID(),UUID.randomUUID(),"Estudar",null,MeasurementType.COUNT,
            BigDecimal.ONE,null,DayPeriod.MORNING,null,1,day,null,Set.of(DayOfWeek.WEDNESDAY));
        habit.setDayPeriods(Set.of(DayPeriod.MORNING,DayPeriod.EVENING));
        habit.cancelOccurrence(day,DayPeriod.MORNING);
        habit.cancelOccurrence(day,DayPeriod.MORNING);
        assertThat(habit.getDayPeriodsFor(day)).containsExactly(DayPeriod.EVENING);
        assertThat(habit.getDayPeriodsFor(day.plusWeeks(1))).containsExactlyInAnyOrder(DayPeriod.MORNING,DayPeriod.EVENING);
        habit.cancelOccurrence(day,DayPeriod.EVENING);
        assertThat(habit.isScheduledFor(day)).isFalse();
        assertThat(habit.isScheduledFor(day.plusWeeks(1))).isTrue();
        habit.restoreOccurrence(day,DayPeriod.MORNING);
        assertThat(habit.isScheduledFor(day)).isTrue();
        assertThat(habit.getDayPeriodsFor(day)).containsExactly(DayPeriod.MORNING);
    }
}

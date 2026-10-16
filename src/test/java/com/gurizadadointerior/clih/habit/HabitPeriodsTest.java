package com.gurizadadointerior.clih.habit.domain;

import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import com.gurizadadointerior.clih.shared.error.ApiException;
import org.junit.jupiter.api.Test;

class HabitPeriodsTest {
 private Habit habit() {
  return new Habit(UUID.randomUUID(), UUID.randomUUID(), "Estudar", null,
   MeasurementType.HOURS, new BigDecimal("1.5"), "horas", DayPeriod.MORNING,
   null, 1, LocalDate.of(2000,1,1), null, Set.of(DayOfWeek.MONDAY));
 }
 @Test void legacyPeriodAndFractionalHoursRemainValid() {
  var habit=habit();
  assertThat(habit.getDayPeriods()).containsExactly(DayPeriod.MORNING);
  assertThat(habit.getMeasurementType()).isEqualTo(MeasurementType.HOURS);
  assertThat(habit.getTargetValue()).isEqualByComparingTo("1.5");
 }
 @Test void multiplePeriodsBelongToOneHabitAndOneDailySchedule() {
  var habit=habit();
  var id=habit.getId();
  habit.setDayPeriods(Set.of(DayPeriod.EVENING,DayPeriod.MORNING));
  assertThat(habit.getDayPeriods()).containsExactlyInAnyOrder(DayPeriod.MORNING,DayPeriod.EVENING);
  assertThat(habit.getDayPeriod()).isEqualTo(DayPeriod.MORNING);
  assertThat(habit.getId()).isEqualTo(id);
  assertThat(habit.getScheduledDays()).containsExactly(DayOfWeek.MONDAY);
 }
 @Test void rejectsEmptyAndAmbiguousPeriods() {
  var habit=habit();
  assertThatThrownBy(()->habit.setDayPeriods(Set.of())).isInstanceOf(ApiException.class);
  assertThatThrownBy(()->habit.setDayPeriods(Set.of(DayPeriod.ANYTIME,DayPeriod.MORNING))).isInstanceOf(ApiException.class);
  habit.setDayPeriods(Set.of(DayPeriod.ANYTIME));
  assertThat(habit.getDayPeriods()).containsExactly(DayPeriod.ANYTIME);
 }
 @Test void archiveRemovesHabitFromScheduleWithoutDestroyingItsData() {
  var habit=habit();
  assertThat(habit.isScheduledFor(LocalDate.of(2026,10,19))).isTrue();
  habit.archive(Instant.now());
  assertThat(habit.isScheduledFor(LocalDate.of(2026,10,19))).isFalse();
  assertThat(habit.getName()).isEqualTo("Estudar");
  assertThat(habit.getArchivedAt()).isNotNull();
 }
}

package com.gurizadadointerior.clih.completion.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.gurizadadointerior.clih.completion.domain.*;
import com.gurizadadointerior.clih.habit.application.HabitApplicationService;
import com.gurizadadointerior.clih.habit.domain.*;
import com.gurizadadointerior.clih.reward.application.*;
import com.gurizadadointerior.clih.reward.domain.StreakCalculator;
import com.gurizadadointerior.clih.user.domain.AppUser;

class PeriodCompletionTest {
    @Test void morningAndEveningAreIndependentAndRecurring() {
        var user = new AppUser("period-test");
        var owner = UUID.randomUUID(); var habitId = UUID.randomUUID();
        ReflectionTestUtils.setField(user, "id", owner);
        var date = LocalDate.of(2026, 10, 19);
        var habit = new Habit(owner, UUID.randomUUID(), "Ler", null, MeasurementType.COUNT,
            BigDecimal.ONE, "vez", DayPeriod.MORNING, null, 1, date, null, Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
        habit.setDayPeriods(Set.of(DayPeriod.MORNING, DayPeriod.EVENING));
        var repo = mock(HabitCompletionRepository.class);
        var habits = mock(HabitApplicationService.class);
        var rewards = mock(RewardApplicationService.class);
        var saved = new ArrayList<HabitCompletion>();
        when(habits.getOwnedActive(owner, habitId)).thenReturn(habit);
        when(repo.existsByUserIdAndHabitIdAndActivityDate(eq(owner), eq(habitId), eq(date), any()))
            .thenAnswer(i -> saved.stream().anyMatch(c -> !c.isUndone() && c.getDayPeriod() == i.getArgument(3)));
        when(repo.saveAndFlush(any())).thenAnswer(i -> { HabitCompletion c = i.getArgument(0);
            if(c.getId() == null) { ReflectionTestUtils.setField(c,"id",UUID.randomUUID()); saved.add(c); } return c; });
        when(repo.findDistinctActivityDatesByUserId(owner)).thenReturn(List.of(date));
        when(rewards.awardHabitCompletion(eq(owner), eq(habit), any(), any()))
            .thenReturn(new RewardApplicationService.RewardResult(10, false, new ProgressView(10,1,0,100,1,1,date)));
        var service = new CompletionApplicationService(repo, habits, rewards, new StreakCalculator(), Clock.fixed(date.atTime(12,0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
        service.complete(user, habitId, "morning", new CompleteHabitCommand(null,null,null,DayPeriod.MORNING));
        assertThat(saved).hasSize(1);
        service.complete(user, habitId, "evening", new CompleteHabitCommand(null,null,null,DayPeriod.EVENING));
        assertThat(saved).extracting(HabitCompletion::getDayPeriod).containsExactly(DayPeriod.MORNING,DayPeriod.EVENING);
        assertThatThrownBy(() -> service.complete(user, habitId, "duplicate", new CompleteHabitCommand(null,null,null,DayPeriod.MORNING))).isInstanceOf(com.gurizadadointerior.clih.shared.error.ApiException.class);
        when(repo.findByIdAndUserIdAndHabitId(saved.getFirst().getId(),owner,habitId)).thenReturn(Optional.of(saved.getFirst()));
        service.undo(user,habitId,saved.getFirst().getId());
        assertThat(saved.getFirst().isUndone()).isTrue();
        assertThat(saved.getLast().isUndone()).isFalse();
        for(int week=0;week<12;week++) {
            assertThat(habit.isScheduledFor(date.plusWeeks(week))).isTrue();
            assertThat(habit.isScheduledFor(date.plusWeeks(week).plusDays(2))).isTrue();
            assertThat(habit.isScheduledFor(date.plusWeeks(week).plusDays(1))).isFalse();
        }
        habit.archive(Instant.now());
        assertThat(habit.isScheduledFor(date.plusWeeks(13))).isFalse();
    }
}

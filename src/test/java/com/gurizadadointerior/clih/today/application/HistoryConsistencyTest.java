package com.gurizadadointerior.clih.today.application;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.gurizadadointerior.clih.area.application.AreaApplicationService;
import com.gurizadadointerior.clih.completion.domain.*;
import com.gurizadadointerior.clih.habit.application.HabitApplicationService;
import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.reward.application.*;
import com.gurizadadointerior.clih.user.domain.AppUser;
class HistoryConsistencyTest {
 @Test void recalculatesStreakAndKeepsDaysWithArchivedActivitiesVisible() {
  var user=mock(AppUser.class);var uid=UUID.randomUUID();var date=LocalDate.of(2026,10,18);
  when(user.getId()).thenReturn(uid);when(user.zoneId()).thenReturn(ZoneOffset.UTC);
  var habits=mock(HabitApplicationService.class);var completions=mock(HabitCompletionRepository.class);
  var areas=mock(AreaApplicationService.class);var rewards=mock(RewardApplicationService.class);
  when(rewards.getProgress(uid)).thenReturn(new ProgressView(40,1,0,100,4,4,date));
  var history=new ArrayList<HabitCompletion>();
  for(int i=0;i<3;i++){
   var c=mock(HabitCompletion.class);when(c.getHabitId()).thenReturn(UUID.randomUUID());when(c.getActivityDate()).thenReturn(date.minusDays(i));when(c.getDayPeriod()).thenReturn(DayPeriod.MORNING);history.add(c);
  }
  when(completions.findDistinctActivityDatesByUserId(uid)).thenReturn(List.of(date,date.minusDays(1),date.minusDays(2)));
  when(completions.findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(uid,date.minusDays(6),date)).thenReturn(history);
  var service=new TodayQueryService(habits,completions,areas,rewards,Clock.fixed(date.atTime(12,0).toInstant(ZoneOffset.UTC),ZoneOffset.UTC));
  var summary=service.getDay(user,date);
  assertThat(summary.progress().currentStreak()).isEqualTo(3);
  assertThat(summary.progress().longestStreak()).isEqualTo(3);
  assertThat(summary.week().stream().filter(day->day.completed()>0).count()).isEqualTo(3);
  assertThat(summary.habits()).isEmpty();
 }
}

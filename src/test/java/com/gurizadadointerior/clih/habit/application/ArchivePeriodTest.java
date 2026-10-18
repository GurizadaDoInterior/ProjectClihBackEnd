package com.gurizadadointerior.clih.habit.application;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.gurizadadointerior.clih.area.application.AreaApplicationService;
import com.gurizadadointerior.clih.habit.domain.*;
import com.gurizadadointerior.clih.user.domain.AppUser;
class ArchivePeriodTest {
 @Test void removesOnlyChosenPeriodAndArchivesLastPeriod() {
  var owner=new AppUser("test");var uid=UUID.randomUUID();var id=UUID.randomUUID();
  ReflectionTestUtils.setField(owner,"id",uid);
  var habit=new Habit(uid,UUID.randomUUID(),"Ler",null,MeasurementType.COUNT,BigDecimal.ONE,null,DayPeriod.MORNING,null,1,LocalDate.of(2026,10,18),null,Set.of(DayOfWeek.SUNDAY));
  habit.setDayPeriods(Set.of(DayPeriod.MORNING,DayPeriod.EVENING));
  var repo=mock(HabitRepository.class);
  when(repo.findByIdAndUserIdAndArchivedAtIsNull(id,uid)).thenReturn(Optional.of(habit));
  var service=new HabitApplicationService(repo,mock(AreaApplicationService.class),Clock.systemUTC());
  service.archivePeriod(owner,id,DayPeriod.MORNING);
  assertThat(habit.getDayPeriods()).containsExactly(DayPeriod.EVENING);
  assertThat(habit.getArchivedAt()).isNull();
  assertThat(habit.isScheduledFor(LocalDate.of(2026,10,25))).isTrue();
  service.archivePeriod(owner,id,DayPeriod.MORNING);
  assertThat(habit.getDayPeriods()).containsExactly(DayPeriod.EVENING);
  service.archivePeriod(owner,id,DayPeriod.EVENING);
  assertThat(habit.getArchivedAt()).isNotNull();
 }
}

package com.gurizadadointerior.clih.habit.application;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.*;
import com.gurizadadointerior.clih.habit.domain.*;
import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.completion.domain.*;
import com.gurizadadointerior.clih.completion.application.CompletionApplicationService;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import com.gurizadadointerior.clih.shared.error.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
class OccurrenceCancellationTest {
 @Test void completedOccurrenceIsUndoneOnlyForChosenHabitAndPeriod() {
  var uid=UUID.randomUUID();var id=UUID.randomUUID();var date=LocalDate.of(2026,10,21);
  var user=mock(AppUser.class);when(user.getId()).thenReturn(uid);
  var habits=mock(HabitApplicationService.class);var repo=mock(HabitCompletionRepository.class);var rewards=mock(RewardApplicationService.class);var undo=mock(CompletionApplicationService.class);
  var morning=mock(HabitCompletion.class);when(morning.getHabitId()).thenReturn(id);when(morning.getDayPeriod()).thenReturn(DayPeriod.MORNING);when(morning.getId()).thenReturn(UUID.randomUUID());
  var evening=mock(HabitCompletion.class);when(evening.getHabitId()).thenReturn(id);when(evening.getDayPeriod()).thenReturn(DayPeriod.EVENING);
  var other=mock(HabitCompletion.class);when(other.getHabitId()).thenReturn(UUID.randomUUID());
  when(repo.findAllByUserIdAndActivityDate(uid,date)).thenReturn(List.of(morning,evening,other));
  var service=new OccurrenceCancellationService(habits,repo,rewards,undo);
  service.setCanceled(user,id,date,DayPeriod.MORNING,true);
  verify(undo).undo(user,id,morning.getId());verifyNoMoreInteractions(undo);
  verify(habits).setOccurrenceCanceled(user,id,date,DayPeriod.MORNING,true);
  service.setCanceled(user,id,date,DayPeriod.MORNING,false);
  verify(habits).setOccurrenceCanceled(user,id,date,DayPeriod.MORNING,false);
  verify(rewards,times(2)).lockProgress(uid);
 }
 @Test void validatesOwnershipBeforeReadingOrUndoingCompletions() {
  var user=mock(AppUser.class);var id=UUID.randomUUID();var uid=UUID.randomUUID();when(user.getId()).thenReturn(uid);
  var habits=mock(HabitApplicationService.class);var repo=mock(HabitCompletionRepository.class);var rewards=mock(RewardApplicationService.class);var undo=mock(CompletionApplicationService.class);
  when(habits.getOwnedActive(uid,id)).thenThrow(new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
  assertThatThrownBy(()->new OccurrenceCancellationService(habits,repo,rewards,undo).setCanceled(user,id,LocalDate.now(),DayPeriod.MORNING,true)).isInstanceOf(ApiException.class);
  verifyNoInteractions(repo,undo);
 }
}

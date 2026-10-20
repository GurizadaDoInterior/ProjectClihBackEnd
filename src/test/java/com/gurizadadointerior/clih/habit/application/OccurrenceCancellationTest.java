package com.gurizadadointerior.clih.habit.application;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import java.time.*;
import java.util.*;
import com.gurizadadointerior.clih.habit.domain.*;
import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import com.gurizadadointerior.clih.shared.error.*;
import org.junit.jupiter.api.Test;
class OccurrenceCancellationTest {
 @Test void completedOccurrenceCannotBeCanceledButUndoCancellationIsAllowed() {
  var uid=UUID.randomUUID(); var id=UUID.randomUUID(); var date=LocalDate.of(2026,10,21);
  var user=mock(AppUser.class); when(user.getId()).thenReturn(uid);
  var habits=mock(HabitApplicationService.class); var completions=mock(HabitCompletionRepository.class); var rewards=mock(RewardApplicationService.class);
  var service=new OccurrenceCancellationService(habits,completions,rewards);
  when(completions.existsByUserIdAndHabitIdAndActivityDate(uid,id,date,DayPeriod.MORNING)).thenReturn(true);
  assertThatThrownBy(()->service.setCanceled(user,id,date,DayPeriod.MORNING,true)).isInstanceOf(ApiException.class);
  verify(habits,never()).setOccurrenceCanceled(any(),any(),any(),any(),anyBoolean());
  service.setCanceled(user,id,date,DayPeriod.MORNING,false);
  verify(habits).setOccurrenceCanceled(user,id,date,DayPeriod.MORNING,false);
  verify(rewards,times(2)).lockProgress(uid);
 }
 @Test void validatesOwnershipBeforeLookingUpCompletions() {
  var user=mock(AppUser.class); var id=UUID.randomUUID(); var uid=UUID.randomUUID();when(user.getId()).thenReturn(uid);
  var habits=mock(HabitApplicationService.class);var completions=mock(HabitCompletionRepository.class);var rewards=mock(RewardApplicationService.class);
  when(habits.getOwnedActive(uid,id)).thenThrow(new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
  assertThatThrownBy(()->new OccurrenceCancellationService(habits,completions,rewards).setCanceled(user,id,LocalDate.now(),DayPeriod.MORNING,true)).isInstanceOf(ApiException.class);
  verifyNoInteractions(completions);
 }
}

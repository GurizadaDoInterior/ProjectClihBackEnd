package com.gurizadadointerior.clih.sleep;

import java.time.LocalDate;
import java.util.UUID;
import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.shared.error.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SleepControllerTest {
 @Test void savesOvernightDurationForAuthenticatedUser() {
  var jdbc=mock(JdbcTemplate.class);
  var users=mock(CurrentUserProvider.class);
  var user=mock(AppUser.class);
  var id=UUID.randomUUID();
  when(users.getCurrentUser()).thenReturn(user);
  when(user.getId()).thenReturn(id);
  when(user.getTimezoneId()).thenReturn("UTC");
  var day=LocalDate.of(2000,1,1);
  var result=new SleepController(jdbc,users).save(new SleepController.Input(day,"23:30","07:00","Boa"));
  assertEquals(450,result.durationMinutes());
  verify(jdbc).update(anyString(),eq(id),eq(day),eq("23:30"),eq("07:00"),eq("Boa"),eq(450));
 }
 @Test void rejectsEqualTimesAndFutureDatesWithoutWriting() {
  var jdbc=mock(JdbcTemplate.class);
  var users=mock(CurrentUserProvider.class);
  var user=mock(AppUser.class);
  when(users.getCurrentUser()).thenReturn(user);
  when(user.getTimezoneId()).thenReturn("UTC");
  var controller=new SleepController(jdbc,users);
  assertThrows(ApiException.class,()->controller.save(new SleepController.Input(LocalDate.of(2000,1,1),"07:00","07:00","Boa")));
  assertThrows(ApiException.class,()->controller.save(new SleepController.Input(LocalDate.now().plusYears(1),"23:00","07:00","Boa")));
  verifyNoInteractions(jdbc);
 }
 @Test void rejectsReversedAndExcessiveHistoryRanges() {
  var jdbc=mock(JdbcTemplate.class);
  var users=mock(CurrentUserProvider.class);
  var controller=new SleepController(jdbc,users);
  var date=LocalDate.of(2000,1,1);
  assertThrows(ApiException.class,()->controller.list(date,date.minusDays(1)));
  assertThrows(ApiException.class,()->controller.list(date,date.plusDays(367)));
  verifyNoInteractions(jdbc,users);
 }
}

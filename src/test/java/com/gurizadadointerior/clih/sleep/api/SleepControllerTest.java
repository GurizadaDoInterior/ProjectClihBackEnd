package com.gurizadadointerior.clih.sleep.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.sleep.api.dto.SaveSleepRequest;
import com.gurizadadointerior.clih.sleep.application.SaveSleepCommand;
import com.gurizadadointerior.clih.sleep.application.SleepApplicationService;
import com.gurizadadointerior.clih.sleep.domain.SleepQuality;
import com.gurizadadointerior.clih.sleep.domain.SleepRecord;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SleepControllerTest {

	@Test
	void savesUsingTheDateFromTheResourcePath() {
		var currentUserProvider = mock(CurrentUserProvider.class);
		var service = mock(SleepApplicationService.class);
		var user = mock(AppUser.class);
		var userId = UUID.randomUUID();
		var date = LocalDate.of(2026, 10, 17);
		var record = new SleepRecord(
			userId, date, LocalTime.parse("23:30"), LocalTime.parse("07:00"), SleepQuality.GOOD
		);
		when(currentUserProvider.getCurrentUser()).thenReturn(user);
		when(service.save(org.mockito.ArgumentMatchers.eq(user), org.mockito.ArgumentMatchers.any()))
			.thenReturn(record);
		var controller = new SleepController(currentUserProvider, service);

		var response = controller.save(date, new SaveSleepRequest("23:30", "07:00", "Boa"));

		var captor = ArgumentCaptor.forClass(SaveSleepCommand.class);
		verify(service).save(org.mockito.ArgumentMatchers.eq(user), captor.capture());
		assertEquals(date, captor.getValue().date());
		assertEquals(SleepQuality.GOOD, captor.getValue().quality());
		assertEquals("GOOD", response.quality());
		assertEquals("Boa", response.qualityLabel());
		assertEquals(450, response.durationMinutes());
	}

	@Test
	void listsHistoryAndReturnsNoContentWhenDeleting() {
		var currentUserProvider = mock(CurrentUserProvider.class);
		var service = mock(SleepApplicationService.class);
		var user = mock(AppUser.class);
		var userId = UUID.randomUUID();
		var date = LocalDate.of(2026, 10, 17);
		var record = new SleepRecord(
			userId, date, LocalTime.parse("00:00"), LocalTime.parse("06:30"), SleepQuality.FAIR
		);
		when(currentUserProvider.getCurrentUser()).thenReturn(user);
		when(service.list(user, date, date)).thenReturn(List.of(record));
		var controller = new SleepController(currentUserProvider, service);

		var history = controller.list(date, date);
		var deletion = controller.delete(date);

		assertEquals(1, history.size());
		assertEquals(HttpStatus.NO_CONTENT, deletion.getStatusCode());
		verify(service).delete(user, date);
	}
}

package com.gurizadadointerior.clih.sleep.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.sleep.domain.SleepQuality;
import com.gurizadadointerior.clih.sleep.domain.SleepRecord;
import com.gurizadadointerior.clih.sleep.domain.SleepRecordRepository;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SleepApplicationServiceTest {

	private SleepRecordRepository repository;
	private AppUser user;
	private SleepApplicationService service;
	private UUID userId;

	@BeforeEach
	void setUp() {
		repository = mock(SleepRecordRepository.class);
		user = mock(AppUser.class);
		userId = UUID.randomUUID();
		when(user.getId()).thenReturn(userId);
		when(user.zoneId()).thenReturn(ZoneId.of("America/Sao_Paulo"));
		var clock = Clock.fixed(Instant.parse("2026-10-18T12:00:00Z"), ZoneOffset.UTC);
		service = new SleepApplicationService(repository, clock);
	}

	@Test
	void savesForTheAuthenticatedUserAndCalculatesDuration() {
		var date = LocalDate.of(2026, 10, 18);
		var result = service.save(user, command(date, "23:30", "07:00"));

		var captor = ArgumentCaptor.forClass(SleepRecord.class);
		verify(repository).save(captor.capture());
		assertSame(result, captor.getValue());
		assertEquals(userId, result.getUserId());
		assertEquals(450, result.durationMinutes());
	}

	@Test
	void rejectsFutureDateInTheUsersTimezone() {
		var futureDate = LocalDate.of(2026, 10, 19);

		assertThrows(ApiException.class, () -> service.save(user, command(futureDate, "23:00", "07:00")));
		verifyNoInteractions(repository);
	}

	@Test
	void rejectsInvalidHistoryRangeBeforeAccessingTheRepository() {
		var from = LocalDate.of(2026, 10, 18);

		assertThrows(ApiException.class, () -> service.list(user, from, from.minusDays(1)));
		assertThrows(ApiException.class, () -> service.list(user, from, from.plusDays(367)));
		verifyNoInteractions(repository);
	}

	@Test
	void returnsOnlyTheRecordOwnedByTheAuthenticatedUser() {
		var date = LocalDate.of(2026, 10, 17);
		var record = new SleepRecord(
			userId, date, LocalTime.parse("23:00"), LocalTime.parse("07:00"), SleepQuality.EXCELLENT
		);
		when(repository.findByUserIdAndDate(userId, date)).thenReturn(Optional.of(record));

		assertSame(record, service.get(user, date));
		verify(repository).findByUserIdAndDate(userId, date);
	}

	private SaveSleepCommand command(LocalDate date, String sleptAt, String wokeAt) {
		return new SaveSleepCommand(
			date,
			LocalTime.parse(sleptAt),
			LocalTime.parse(wokeAt),
			SleepQuality.GOOD
		);
	}
}

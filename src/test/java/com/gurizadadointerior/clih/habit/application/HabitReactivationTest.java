package com.gurizadadointerior.clih.habit.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.gurizadadointerior.clih.area.application.AreaApplicationService;
import com.gurizadadointerior.clih.habit.domain.*;
import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class HabitReactivationTest {

	@Test
	void refusesReactivationInAnArchivedAreaButAllowsMovingToAnActiveArea() {
		var user = mock(AppUser.class);
		var userId = UUID.randomUUID();
		var habitId = UUID.randomUUID();
		var oldAreaId = UUID.randomUUID();
		var newAreaId = UUID.randomUUID();
		when(user.getId()).thenReturn(userId);
		var habit = new Habit(userId, oldAreaId, "Ler", null, MeasurementType.COUNT,
			BigDecimal.ONE, null, DayPeriod.MORNING, null, 1,
			LocalDate.of(2026, 10, 18), null, Set.of(DayOfWeek.SUNDAY));
		habit.update(null, null, null, null, null, null, null, null, null, null, null, null, false);
		var repository = mock(HabitRepository.class);
		var areas = mock(AreaApplicationService.class);
		when(repository.findByIdAndUserIdAndArchivedAtIsNull(habitId, userId)).thenReturn(Optional.of(habit));
		when(areas.getOwnedActive(userId, oldAreaId)).thenThrow(new ApiException(ErrorCode.RESOURCE_NOT_FOUND));
		var service = new HabitApplicationService(repository, areas, Clock.systemUTC());

		assertThatThrownBy(() -> service.update(user, habitId, reactivate(null))).isInstanceOf(ApiException.class);
		assertThat(habit.isActive()).isFalse();
		verify(repository, never()).save(any());

		service.update(user, habitId, reactivate(newAreaId));
		assertThat(habit.isActive()).isTrue();
		assertThat(habit.getAreaId()).isEqualTo(newAreaId);
		verify(areas).getOwnedActive(userId, newAreaId);
		verify(repository).save(habit);
	}

	private UpdateHabitCommand reactivate(UUID areaId) {
		return new UpdateHabitCommand(areaId, null, null, null, null, null, null,
			null, null, null, null, null, true, null);
	}
}

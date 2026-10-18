package com.gurizadadointerior.clih.evolution.api;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.gurizadadointerior.clih.evolution.application.EvolutionQueryService;
import com.gurizadadointerior.clih.evolution.application.view.EvolutionResponse;
import com.gurizadadointerior.clih.evolution.application.view.EvolutionSummaryView;
import com.gurizadadointerior.clih.evolution.application.view.SelectedDayView;
import com.gurizadadointerior.clih.evolution.application.view.SleepComparisonView;
import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EvolutionControllerTest {

	@Test
	void delegatesTheAuthenticatedUsersRequestedPeriod() {
		var currentUserProvider = mock(CurrentUserProvider.class);
		var service = mock(EvolutionQueryService.class);
		var user = mock(AppUser.class);
		var month = YearMonth.of(2026, 10);
		var selectedDate = LocalDate.of(2026, 10, 17);
		var response = new EvolutionResponse(
			month.toString(),
			selectedDate,
			new EvolutionSummaryView(0, 0, 0, 0),
			List.of(),
			new SelectedDayView(selectedDate, null, List.of()),
			new SleepComparisonView(selectedDate.minusDays(6), selectedDate, null, 0, List.of()),
			List.of()
		);
		when(currentUserProvider.getCurrentUser()).thenReturn(user);
		when(service.getEvolution(user, month, selectedDate)).thenReturn(response);
		var controller = new EvolutionController(currentUserProvider, service);

		assertSame(response, controller.getEvolution(month, selectedDate));
		verify(service).getEvolution(user, month, selectedDate);
	}
}

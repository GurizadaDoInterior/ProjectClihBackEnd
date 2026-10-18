package com.gurizadadointerior.clih.evolution.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.gurizadadointerior.clih.area.application.AreaApplicationService;
import com.gurizadadointerior.clih.area.domain.Area;
import com.gurizadadointerior.clih.completion.domain.HabitCompletion;
import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import com.gurizadadointerior.clih.evolution.application.view.EvolutionDayStatus;
import com.gurizadadointerior.clih.habit.application.HabitApplicationService;
import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.habit.domain.Habit;
import com.gurizadadointerior.clih.reward.application.ProgressView;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.sleep.domain.SleepQuality;
import com.gurizadadointerior.clih.sleep.domain.SleepRecord;
import com.gurizadadointerior.clih.sleep.domain.SleepRecordRepository;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EvolutionQueryServiceTest {

	private HabitApplicationService habitService;
	private HabitCompletionRepository completionRepository;
	private AreaApplicationService areaService;
	private RewardApplicationService rewardService;
	private SleepRecordRepository sleepRepository;
	private AppUser user;
	private UUID userId;
	private EvolutionQueryService service;

	@BeforeEach
	void setUp() {
		habitService = mock(HabitApplicationService.class);
		completionRepository = mock(HabitCompletionRepository.class);
		areaService = mock(AreaApplicationService.class);
		rewardService = mock(RewardApplicationService.class);
		sleepRepository = mock(SleepRecordRepository.class);
		user = mock(AppUser.class);
		userId = UUID.randomUUID();
		when(user.getId()).thenReturn(userId);
		when(user.zoneId()).thenReturn(ZoneId.of("America/Sao_Paulo"));
		when(rewardService.getProgress(userId)).thenReturn(new ProgressView(250, 3, 200, 300, 3, 8, null));
		when(completionRepository.findDistinctActivityDatesByUserId(userId)).thenReturn(List.of(LocalDate.of(2026,10,16),LocalDate.of(2026,10,17),LocalDate.of(2026,10,18)));
		var clock = Clock.fixed(Instant.parse("2026-10-18T12:00:00Z"), ZoneOffset.UTC);
		service = new EvolutionQueryService(
			habitService, completionRepository, areaService, rewardService, sleepRepository, clock
		);
	}

	@Test
	void buildsCalendarSelectedDaySleepComparisonAndAreaProgress() {
		var month = YearMonth.of(2026, 10);
		var selectedDate = LocalDate.of(2026, 10, 17);
		var areaId = UUID.randomUUID();
		var habitId = UUID.randomUUID();
		var habit = dailyHabit(habitId, areaId);
		var area = area(areaId);
		var completion = completion(habitId, selectedDate);
		var sleep = new SleepRecord(
			userId, selectedDate, LocalTime.parse("00:00"), LocalTime.parse("06:30"), SleepQuality.FAIR
		);
		when(habitService.findAllActive(user)).thenReturn(List.of(habit));
		when(areaService.list(user)).thenReturn(List.of(area));
		when(completionRepository.findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(
			userId, month.atDay(1), month.atEndOfMonth()
		)).thenReturn(List.of(completion));
		when(sleepRepository.findAllByUserIdAndDateBetween(userId, month.atDay(1), month.atEndOfMonth()))
			.thenReturn(List.of(sleep));

		var response = service.getEvolution(user, month, selectedDate);

		assertEquals("2026-10", response.month());
		assertEquals(selectedDate, response.selectedDate());
		assertEquals(31, response.calendar().size());
		var completedDay = response.calendar().get(16);
		assertEquals(EvolutionDayStatus.COMPLETE, completedDay.status());
		assertEquals(1, completedDay.plannedActivities());
		assertEquals(1, completedDay.completedActivities());
		assertEquals(100, completedDay.completionPercentage());
		assertEquals(390, completedDay.sleepDurationMinutes());
		assertEquals("FAIR", completedDay.sleepQuality());
		assertEquals(EvolutionDayStatus.PENDING, response.calendar().get(17).status());
		assertEquals(EvolutionDayStatus.FUTURE, response.calendar().get(18).status());
		assertEquals(1, response.summary().activeDaysInMonth());
		assertEquals(0, response.summary().activitiesCompletedToday());
		assertEquals(3, response.summary().currentStreak());
		assertEquals(8, response.summary().longestStreak());
		assertEquals(390, response.selectedDay().sleep().durationMinutes());
		assertEquals(1, response.selectedDay().activities().size());
		assertTrue(response.selectedDay().activities().getFirst().completed());
		assertEquals(390, response.sleepComparison().averageMinutes());
		assertEquals(1, response.sleepComparison().registeredDays());
		assertEquals(7, response.sleepComparison().days().size());
		assertEquals(1, response.areas().size());
		assertEquals(100, response.areas().getFirst().completionPercentage());
		verify(completionRepository).findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(
			userId, month.atDay(1), month.atEndOfMonth()
		);
		verify(sleepRepository).findAllByUserIdAndDateBetween(userId, month.atDay(1), month.atEndOfMonth());
	}

	@Test
	void representsDaysWithoutPlannedActivitiesOrSleepWithoutFalseZeroes() {
		var month = YearMonth.of(2026, 9);
		var selectedDate = month.atEndOfMonth();
		when(habitService.findAllActive(user)).thenReturn(List.of());
		when(areaService.list(user)).thenReturn(List.of());

		var response = service.getEvolution(user, month, selectedDate);

		assertEquals(30, response.calendar().size());
		assertEquals(EvolutionDayStatus.EMPTY, response.calendar().getLast().status());
		assertNull(response.calendar().getLast().completionPercentage());
		assertNull(response.selectedDay().sleep());
		assertNull(response.sleepComparison().averageMinutes());
		assertEquals(0, response.sleepComparison().registeredDays());
		assertTrue(response.selectedDay().activities().isEmpty());
		assertFalse(response.calendar().stream().anyMatch(day -> day.completedActivities() > 0));
		verify(completionRepository).findAllByUserIdAndActivityDate(userId, LocalDate.of(2026, 10, 18));
	}

	@Test
	void buildsAllDaysOfALeapYearMonthAndDefaultsToItsLastDay() {
		var response = service.getEvolution(user, YearMonth.of(2024, 2), null);

		assertEquals(29, response.calendar().size());
		assertEquals(LocalDate.of(2024, 2, 29), response.selectedDate());
		assertEquals(LocalDate.of(2024, 2, 29), response.calendar().getLast().date());
	}

	@Test
	void rejectsFutureMonthBeforeReadingPrivateData() {
		assertThrows(
			ApiException.class,
			() -> service.getEvolution(user, YearMonth.of(2026, 11), LocalDate.of(2026, 11, 1))
		);

		verifyNoInteractions(habitService, completionRepository, areaService, rewardService, sleepRepository);
	}

	@Test
	void rejectsSelectedDateOutsideTheRequestedMonth() {
		assertThrows(
			ApiException.class,
			() -> service.getEvolution(user, YearMonth.of(2026, 10), LocalDate.of(2026, 9, 30))
		);

		verifyNoInteractions(habitService, completionRepository, areaService, rewardService, sleepRepository);
	}

    @Test void countsEachPeriodIndependently() {
        var date = LocalDate.of(2026,10,18);
        var habitId = UUID.randomUUID(); var areaId = UUID.randomUUID();
        var habit = dailyHabit(habitId,areaId);
        when(habit.getDayPeriods()).thenReturn(Set.of(DayPeriod.MORNING,DayPeriod.EVENING));
        when(habitService.findAllActive(user)).thenReturn(List.of(habit));
        var testArea = area(areaId);
        var testCompletion = completion(habitId,date);
        when(areaService.list(user)).thenReturn(List.of(testArea));
        when(completionRepository.findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(userId,date.withDayOfMonth(1),date.withDayOfMonth(31))).thenReturn(List.of(testCompletion));
        var response=service.getEvolution(user,YearMonth.of(2026,10),date);
        assertEquals(2,response.selectedDay().activities().size());
        assertEquals(1,response.selectedDay().activities().stream().filter(a -> a.completed()).count());
        var day=response.calendar().stream().filter(d -> d.date().equals(date)).findFirst().orElseThrow();
        assertEquals(2,day.plannedActivities());
        assertEquals(1,day.completedActivities());
        assertEquals(EvolutionDayStatus.PARTIAL,day.status());
    }

	private Habit dailyHabit(UUID habitId, UUID areaId) {
		var habit = mock(Habit.class);
		when(habit.getId()).thenReturn(habitId);
		when(habit.getAreaId()).thenReturn(areaId);
		when(habit.getName()).thenReturn("Estudar Java");
		when(habit.getDayPeriods()).thenReturn(Set.of(DayPeriod.MORNING));
		when(habit.isScheduledFor(any(LocalDate.class))).thenReturn(true);
		return habit;
	}

	private Area area(UUID areaId) {
		var area = mock(Area.class);
		when(area.getId()).thenReturn(areaId);
		when(area.getName()).thenReturn("Estudos");
		when(area.getColor()).thenReturn("#3366FF");
		when(area.getIcon()).thenReturn("book");
		return area;
	}

	private HabitCompletion completion(UUID habitId, LocalDate date) {
		var completion = mock(HabitCompletion.class);
		when(completion.getId()).thenReturn(UUID.randomUUID());
		when(completion.getHabitId()).thenReturn(habitId);
        when(completion.getDayPeriod()).thenReturn(DayPeriod.MORNING);
		when(completion.getActivityDate()).thenReturn(date);
		when(completion.getCompletedAt()).thenReturn(Instant.parse("2026-10-17T12:00:00Z"));
		return completion;
	}
}

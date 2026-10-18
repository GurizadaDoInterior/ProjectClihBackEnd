package com.gurizadadointerior.clih.evolution.application;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.gurizadadointerior.clih.area.application.AreaApplicationService;
import com.gurizadadointerior.clih.area.domain.Area;
import com.gurizadadointerior.clih.completion.domain.HabitCompletion;
import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import com.gurizadadointerior.clih.evolution.application.view.CalendarDayView;
import com.gurizadadointerior.clih.evolution.application.view.EvolutionAreaView;
import com.gurizadadointerior.clih.evolution.application.view.EvolutionDayStatus;
import com.gurizadadointerior.clih.evolution.application.view.EvolutionResponse;
import com.gurizadadointerior.clih.evolution.application.view.EvolutionSummaryView;
import com.gurizadadointerior.clih.evolution.application.view.SelectedActivityView;
import com.gurizadadointerior.clih.evolution.application.view.SelectedDayView;
import com.gurizadadointerior.clih.evolution.application.view.SleepComparisonView;
import com.gurizadadointerior.clih.evolution.application.view.SleepDayView;
import com.gurizadadointerior.clih.habit.application.HabitApplicationService;
import com.gurizadadointerior.clih.habit.domain.Habit;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.sleep.domain.SleepRecord;
import com.gurizadadointerior.clih.sleep.domain.SleepRecordRepository;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvolutionQueryService {

	private final HabitApplicationService habitService;
	private final HabitCompletionRepository completionRepository;
	private final AreaApplicationService areaService;
	private final RewardApplicationService rewardService;
	private final SleepRecordRepository sleepRepository;
	private final Clock clock;

	public EvolutionQueryService(
		HabitApplicationService habitService,
		HabitCompletionRepository completionRepository,
		AreaApplicationService areaService,
		RewardApplicationService rewardService,
		SleepRecordRepository sleepRepository,
		Clock clock
	) {
		this.habitService = habitService;
		this.completionRepository = completionRepository;
		this.areaService = areaService;
		this.rewardService = rewardService;
		this.sleepRepository = sleepRepository;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public EvolutionResponse getEvolution(AppUser user, YearMonth requestedMonth, LocalDate requestedDate) {
		var today = LocalDate.now(clock.withZone(user.zoneId()));
		var month = requestedMonth == null ? YearMonth.from(today) : requestedMonth;
		validateMonth(month, today);
		var selectedDate = resolveSelectedDate(month, requestedDate, today);
		var monthStart = month.atDay(1);
		var monthEnd = month.atEndOfMonth();

		var habits = habitService.findAllActive(user);
		var areas = areaService.list(user);
		var areasById = areas.stream().collect(Collectors.toMap(Area::getId, Function.identity()));
		var completionsByDate = groupCompletions(completionRepository
			.findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(user.getId(), monthStart, monthEnd));

		var comparisonStart = selectedDate.minusDays(6);
		var sleepQueryStart = comparisonStart.isBefore(monthStart) ? comparisonStart : monthStart;
		var sleepByDate = sleepRepository.findAllByUserIdAndDateBetween(user.getId(), sleepQueryStart, monthEnd)
			.stream()
			.collect(Collectors.toMap(SleepRecord::getDate, Function.identity()));

		var calendar = buildCalendar(month, today, habits, completionsByDate, sleepByDate);
		var selectedCompletions = completionsByDate.getOrDefault(selectedDate, Map.of());
		var selectedHabits = scheduledFor(habits, selectedDate);
		var selectedDay = buildSelectedDay(selectedDate, selectedHabits, selectedCompletions, areasById, sleepByDate);
		var sleepComparison = buildSleepComparison(comparisonStart, selectedDate, sleepByDate);
		var areaProgress = buildAreaProgress(areas, selectedHabits, selectedCompletions);
		var progress = rewardService.getProgress(user.getId()).withHistory(completionRepository.findDistinctActivityDatesByUserId(user.getId()),today);
		var todayCompletions = completionsForToday(user, today, month, completionsByDate);
		var completedToday = completedCount(scheduledFor(habits, today), todayCompletions);
		var activeDays = Math.toIntExact(calendar.stream().filter(day -> day.completedActivities() > 0).count());

		return new EvolutionResponse(
			month.toString(),
			selectedDate,
			new EvolutionSummaryView(
				completedToday, activeDays, progress.currentStreak(), progress.longestStreak()
			),
			calendar,
			selectedDay,
			sleepComparison,
			areaProgress
		);
	}

	private void validateMonth(YearMonth month, LocalDate today) {
		if (month.isAfter(YearMonth.from(today))) {
			throw new ApiException(ErrorCode.INVALID_DATE_RANGE, "O mês de evolução não pode estar no futuro.");
		}
	}

	private LocalDate resolveSelectedDate(YearMonth month, LocalDate requestedDate, LocalDate today) {
		var selectedDate = requestedDate == null
			? (month.equals(YearMonth.from(today)) ? today : month.atEndOfMonth())
			: requestedDate;
		if (!YearMonth.from(selectedDate).equals(month) || selectedDate.isAfter(today)) {
			throw new ApiException(ErrorCode.INVALID_DATE_RANGE, "A data selecionada deve pertencer ao mês e não pode estar no futuro.");
		}
		return selectedDate;
	}

	private Map<LocalDate, Map<String, HabitCompletion>> groupCompletions(List<HabitCompletion> completions) {
		return completions.stream().collect(Collectors.groupingBy(
			HabitCompletion::getActivityDate,
			Collectors.toMap(c -> occurrenceKey(c.getHabitId(), c.getDayPeriod()), Function.identity(), (first, ignored) -> first)
		));
	}

	private List<CalendarDayView> buildCalendar(
		YearMonth month,
		LocalDate today,
		List<Habit> habits,
		Map<LocalDate, Map<String, HabitCompletion>> completionsByDate,
		Map<LocalDate, SleepRecord> sleepByDate
	) {
		return IntStream.rangeClosed(1, month.lengthOfMonth())
			.mapToObj(month::atDay)
			.map(date -> {
				var scheduled = scheduledFor(habits, date);
				var completed = completionsByDate.getOrDefault(date, Map.of()).size();
				var planned = Math.max(plannedCount(scheduled),completed);
				var sleep = sleepByDate.get(date);
				return new CalendarDayView(
					date,
					statusFor(date, today, planned, completed),
					planned,
					completed,
					percentage(completed, planned),
					sleep == null ? null : sleep.durationMinutes(),
					sleep == null ? null : sleep.getQuality().name(),
					sleep == null ? null : sleep.getQuality().label()
				);
			})
			.toList();
	}

	private SelectedDayView buildSelectedDay(
		LocalDate date,
		List<Habit> habits,
		Map<String, HabitCompletion> completions,
		Map<UUID, Area> areas,
		Map<LocalDate, SleepRecord> sleepByDate
	) {
		var activities = habits.stream().flatMap(habit -> habit.getDayPeriods().stream().sorted().map(period -> {
			var area = areas.get(habit.getAreaId());
			var completion = completions.get(occurrenceKey(habit.getId(), period));
			return new SelectedActivityView(
				habit.getId(),
				habit.getName(),
				habit.getAreaId(),
				area == null ? null : area.getName(),
				area == null ? null : area.getColor(),
				area == null ? null : area.getIcon(),
				java.util.Set.of(period),
				habit.getPreferredTime(),
				completion != null,
				completion == null ? null : completion.getId(),
				completion == null ? null : completion.getCompletedAt()
			);
		})).toList();
		var sleep = sleepByDate.get(date);
		return new SelectedDayView(date, sleep == null ? null : SleepDayView.from(sleep), activities);
	}

	private SleepComparisonView buildSleepComparison(
		LocalDate from,
		LocalDate to,
		Map<LocalDate, SleepRecord> sleepByDate
	) {
		var days = IntStream.rangeClosed(0, 6)
			.mapToObj(from::plusDays)
			.map(date -> {
				var sleep = sleepByDate.get(date);
				return sleep == null ? SleepDayView.empty(date) : SleepDayView.from(sleep);
			})
			.toList();
		var registered = days.stream().filter(day -> day.durationMinutes() != null).toList();
		var average = registered.isEmpty()
			? null
			: (int) Math.round(registered.stream().mapToInt(SleepDayView::durationMinutes).average().orElseThrow());
		return new SleepComparisonView(from, to, average, registered.size(), days);
	}

	private List<EvolutionAreaView> buildAreaProgress(
		List<Area> areas,
		List<Habit> habits,
		Map<String, HabitCompletion> completions
	) {
		return areas.stream().map(area -> {
			var areaHabits = habits.stream().filter(habit -> habit.getAreaId().equals(area.getId())).toList();
			var completed = completedCount(areaHabits, completions);
			return new EvolutionAreaView(
				area.getId(),
				area.getName(),
				area.getColor(),
				area.getIcon(),
				plannedCount(areaHabits),
				completed,
				percentage(completed, plannedCount(areaHabits))
			);
		}).toList();
	}

	private Map<String, HabitCompletion> completionsForToday(
		AppUser user,
		LocalDate today,
		YearMonth requestedMonth,
		Map<LocalDate, Map<String, HabitCompletion>> completionsByDate
	) {
		if (requestedMonth.equals(YearMonth.from(today))) {
			return completionsByDate.getOrDefault(today, Map.of());
		}
		return completionRepository.findAllByUserIdAndActivityDate(user.getId(), today).stream()
			.collect(Collectors.toMap(c -> occurrenceKey(c.getHabitId(), c.getDayPeriod()), Function.identity(), (first, ignored) -> first));
	}

	private List<Habit> scheduledFor(List<Habit> habits, LocalDate date) {
		return habits.stream().filter(habit -> habit.isScheduledFor(date)).toList();
	}

	private int completedCount(List<Habit> habits, Map<String, HabitCompletion> completions) {
		return Math.toIntExact(habits.stream().flatMap(habit -> habit.getDayPeriods().stream().map(period -> occurrenceKey(habit.getId(), period))).filter(completions::containsKey).count());
	}

    private int plannedCount(List<Habit> habits) { return habits.stream().mapToInt(h -> h.getDayPeriods().size()).sum(); }
    private String occurrenceKey(UUID id, com.gurizadadointerior.clih.habit.domain.DayPeriod period) { return id + ":" + period; }

	private Integer percentage(int completed, int planned) {
		return planned == 0 ? null : (int) Math.round((completed * 100.0) / planned);
	}

	private EvolutionDayStatus statusFor(LocalDate date, LocalDate today, int planned, int completed) {
		if (date.isAfter(today)) return EvolutionDayStatus.FUTURE;
		if (planned == 0) return EvolutionDayStatus.EMPTY;
		if (completed >= planned) return EvolutionDayStatus.COMPLETE;
		if (date.equals(today) && completed == 0) return EvolutionDayStatus.PENDING;
		if (completed > 0) return EvolutionDayStatus.PARTIAL;
		return EvolutionDayStatus.MISSED;
	}
}

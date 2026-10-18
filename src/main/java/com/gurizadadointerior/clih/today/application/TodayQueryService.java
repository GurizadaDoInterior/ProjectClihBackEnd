package com.gurizadadointerior.clih.today.application;

import com.gurizadadointerior.clih.habit.domain.DayPeriod;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.gurizadadointerior.clih.area.application.AreaApplicationService;
import com.gurizadadointerior.clih.completion.domain.HabitCompletion;
import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import com.gurizadadointerior.clih.habit.domain.Habit;
import com.gurizadadointerior.clih.habit.application.HabitApplicationService;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import com.gurizadadointerior.clih.today.application.view.AreaProgressView;
import com.gurizadadointerior.clih.today.application.view.DayStatus;
import com.gurizadadointerior.clih.today.application.view.TodayAreaResponse;
import com.gurizadadointerior.clih.today.application.view.TodayHabitResponse;
import com.gurizadadointerior.clih.today.application.view.TodayResponse;
import com.gurizadadointerior.clih.today.application.view.TodaySummaryResponse;
import com.gurizadadointerior.clih.today.application.view.TodayWeekDayResponse;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TodayQueryService {
	private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

	private final HabitApplicationService habitService;
	private final HabitCompletionRepository completionRepository;
	private final AreaApplicationService areaService;
	private final RewardApplicationService rewardService;
	private final Clock clock;

	public TodayQueryService(
		HabitApplicationService habitService,
		HabitCompletionRepository completionRepository,
		AreaApplicationService areaService,
		RewardApplicationService rewardService,
		Clock clock
	) {
		this.habitService = habitService;
		this.completionRepository = completionRepository;
		this.areaService = areaService;
		this.rewardService = rewardService;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public TodayResponse getDay(AppUser user, LocalDate date) {
		var allHabits = habitService.findAllActive(user);
		var habits = allHabits.stream().filter(habit -> habit.isScheduledFor(date)).toList();
		Map<String, HabitCompletion> completions = completionRepository
			.findAllByUserIdAndActivityDate(user.getId(), date)
			.stream()
			.collect(Collectors.toMap(c -> occurrenceKey(c.getHabitId(), c.getDayPeriod()), Function.identity()));
		var areas = areaService.list(user).stream()
			.collect(Collectors.toMap(area -> area.getId(), TodayAreaResponse::from));

		var items = habits.stream().flatMap(habit -> habit.getDayPeriods().stream().sorted().map(period -> {
			var completion = completions.get(occurrenceKey(habit.getId(), period));
			return new TodayHabitResponse(
				habit.getId(), habit.getName(), areas.get(habit.getAreaId()), habit.getMeasurementType(),
				habit.getTargetValue(), habit.getUnit(), period, habit.getPreferredTime(),
				completion != null,
				completion == null ? null : completion.getId(),
				completion == null ? null : completion.getCompletedAt(),
				completion == null ? 0 : completion.getXpAwarded(), Set.of(period), period
			);
		})).toList();

		var completed = Math.toIntExact(items.stream().filter(TodayHabitResponse::completed).count());
		var percentage = items.isEmpty() ? 0 : (int) Math.round((completed * 100.0) / items.size());
		var week = buildWeek(user, date, allHabits);
		var areaProgress = buildAreaProgress(areas, habits, completions);

		return new TodayResponse(
			date,
			user.getDisplayName(),
			rewardService.getProgress(user.getId()).withHistory(completionRepository.findDistinctActivityDatesByUserId(user.getId()),LocalDate.now(clock.withZone(user.zoneId()))),
			new TodaySummaryResponse(items.size(), completed, percentage),
			week,
			areaProgress,
			items
		);
	}

	private List<TodayWeekDayResponse> buildWeek(AppUser user, LocalDate selectedDate, List<Habit> habits) {
		var start = selectedDate.with(DayOfWeek.MONDAY);
		var end = start.plusDays(6);
		Map<LocalDate, Set<String>> completedByDate = completionRepository
			.findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(user.getId(), start, end)
			.stream()
			.collect(Collectors.groupingBy(
				HabitCompletion::getActivityDate,
				Collectors.mapping(c -> occurrenceKey(c.getHabitId(), c.getDayPeriod()), Collectors.toSet())
			));
		var today = LocalDate.now(clock.withZone(user.zoneId()));

		return IntStream.range(0, 7)
			.mapToObj(start::plusDays)
			.map(day -> {
				var plannedHabitIds = habits.stream()
					.filter(habit -> habit.isScheduledFor(day))
					.flatMap(habit -> habit.getDayPeriods().stream().map(period -> occurrenceKey(habit.getId(), period)))
					.collect(Collectors.toSet());
				var completed = completedByDate.getOrDefault(day, Set.of()).size();
                var planned = Math.max(plannedHabitIds.size(),completed);
				var progress = percentage(completed, planned);
				return new TodayWeekDayResponse(
					day,
					day.getDayOfWeek().getDisplayName(TextStyle.SHORT, PT_BR).toUpperCase(PT_BR).replace(".", ""),
					statusFor(day, today, planned, completed),
					day.equals(today),
					planned,
					completed,
					progress
				);
			})
			.toList();
	}

	private List<AreaProgressView> buildAreaProgress(
		Map<UUID, TodayAreaResponse> areas,
		List<Habit> habits,
		Map<String, HabitCompletion> completions
	) {
		return areas.values().stream()
			.sorted(Comparator.comparingInt(TodayAreaResponse::position))
			.map(area -> {
				var planned = habits.stream().filter(habit -> habit.getAreaId().equals(area.id())).mapToInt(habit -> habit.getDayPeriods().size()).sum();
				var completed = Math.toIntExact(habits.stream()
					.filter(habit -> habit.getAreaId().equals(area.id()))
					.flatMap(habit -> habit.getDayPeriods().stream().map(period -> occurrenceKey(habit.getId(), period)))
                    .filter(completions::containsKey)
					.count());
				return new AreaProgressView(area, planned, completed, percentage(completed, planned));
			})
			.toList();
	}

    private static String occurrenceKey(UUID id, DayPeriod period) { return id + ":" + period; }

	private DayStatus statusFor(LocalDate date, LocalDate today, int planned, int completed) {
		if (date.isAfter(today)) return DayStatus.FUTURE;
		if (planned == 0) return DayStatus.EMPTY;
		if (completed >= planned) return DayStatus.COMPLETE;
		if (date.equals(today) && completed == 0) return DayStatus.PENDING;
		if (completed > 0) return DayStatus.PARTIAL;
		return DayStatus.MISSED;
	}

	private int percentage(int completed, int planned) {
		return planned == 0 ? 0 : (int) Math.round((completed * 100.0) / planned);
	}
}

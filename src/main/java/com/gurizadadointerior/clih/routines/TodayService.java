package com.gurizadadointerior.clih.routines;

import com.gurizadadointerior.clih.progress.HabitCompletionRepository;
import com.gurizadadointerior.clih.shared.ResourceNotFoundException;
import com.gurizadadointerior.clih.users.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TodayService {

    static final UUID DEMO_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitCompletionRepository completionRepository;

    public TodayService(
            UserRepository userRepository,
            HabitRepository habitRepository,
            HabitCompletionRepository completionRepository
    ) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
    }

    @Transactional(readOnly = true)
    public TodaySummary getToday() {
        var user = userRepository.findById(DEMO_USER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário de demonstração não encontrado."));
        var zoneId = ZoneId.of(user.getTimezone());
        var today = LocalDate.now(zoneId);
        var start = today.atStartOfDay(zoneId).toInstant();
        var end = today.plusDays(1).atStartOfDay(zoneId).toInstant();
        var completedHabitIds = completionRepository
                .findByUserIdAndCompletedAtBetween(DEMO_USER_ID, start, end)
                .stream()
                .map(completion -> completion.getHabit().getId())
                .collect(Collectors.toUnmodifiableSet());
        var habits = habitRepository.findByUserIdAndActiveTrueOrderByPosition(DEMO_USER_ID);

        var activities = habits.stream()
                .map(habit -> new TodaySummary.Activity(
                        habit.getId(),
                        habit.getName(),
                        habit.getTargetLabel(),
                        habit.getArea().getSlug(),
                        completedHabitIds.contains(habit.getId())
                ))
                .toList();

        return new TodaySummary(
                user.getDisplayName(),
                user.getStreakDays(),
                user.getLevel(),
                user.getXp(),
                1800,
                buildWeek(today),
                buildAreaBalance(habits, completedHabitIds),
                activities
        );
    }

    private List<TodaySummary.WeekDay> buildWeek(LocalDate today) {
        var monday = today.with(DayOfWeek.MONDAY);
        return IntStream.range(0, 7)
                .mapToObj(monday::plusDays)
                .map(date -> new TodaySummary.WeekDay(
                        date.getDayOfWeek().getDisplayName(TextStyle.SHORT, PT_BR).toUpperCase(PT_BR).replace(".", ""),
                        weekState(date, today)
                ))
                .toList();
    }

    private String weekState(LocalDate date, LocalDate today) {
        if (date.equals(today)) return "current";
        if (date.isAfter(today)) return "empty";
        return date.getDayOfWeek().getValue() <= 3 ? "complete" : "partial";
    }

    private Map<String, Double> buildAreaBalance(List<Habit> habits, Set<UUID> completedHabitIds) {
        var totals = habits.stream().collect(Collectors.groupingBy(habit -> habit.getArea().getSlug(), Collectors.counting()));
        var completed = habits.stream()
                .filter(habit -> completedHabitIds.contains(habit.getId()))
                .collect(Collectors.groupingBy(habit -> habit.getArea().getSlug(), Collectors.counting()));
        var result = new LinkedHashMap<String, Double>();

        for (var slug : List.of("study", "health", "reading", "sleep")) {
            var total = totals.getOrDefault(slug, 0L);
            var value = total == 0 ? 0.0 : completed.getOrDefault(slug, 0L).doubleValue() / total;
            result.put(slug, Math.max(0.25, value));
        }
        return result;
    }
}

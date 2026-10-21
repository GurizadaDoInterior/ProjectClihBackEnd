package com.gurizadadointerior.clih.habit.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.gurizadadointerior.clih.area.application.AreaApplicationService;
import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.habit.domain.Habit;
import com.gurizadadointerior.clih.habit.domain.HabitRepository;
import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HabitApplicationService {

	private final HabitRepository repository;
	private final AreaApplicationService areaService;
	private final Clock clock;

	public HabitApplicationService(HabitRepository repository, AreaApplicationService areaService, Clock clock) {
		this.repository = repository;
		this.areaService = areaService;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public List<Habit> list(AppUser user) {
		return sortedHabits(user.getId());
	}

	@Transactional
	public Habit create(AppUser user, CreateHabitCommand command) {
		areaService.getOwnedActive(user.getId(), command.areaId());
		var startDate = command.startDate() == null ? LocalDate.now(clock.withZone(user.zoneId())) : command.startDate();
		validateDateRange(startDate, command.endDate());

		var habit = new Habit(
			user.getId(), command.areaId(), command.name().trim(), normalize(command.description()),
			command.measurementType(), command.targetValue(), normalize(command.unit()), command.dayPeriod(),
			command.preferredTime(), repository.findMaxPositionByUserId(user.getId()) + 1,
			startDate, command.endDate(), command.scheduledDays()
		);
		if (command.dayPeriods() != null) habit.setDayPeriods(command.dayPeriods());
		return repository.save(habit);
	}

	@Transactional
	public Habit update(AppUser user, UUID habitId, UpdateHabitCommand command) {
		var habit = getOwnedActive(user.getId(), habitId);
		if (command.areaId() != null || Boolean.TRUE.equals(command.active())) {
			var areaId = command.areaId() == null ? habit.getAreaId() : command.areaId();
			areaService.getOwnedActive(user.getId(), areaId);
		}
		var startDate = command.startDate() == null ? habit.getStartDate() : command.startDate();
		var endDate = command.endDate() == null ? habit.getEndDate() : command.endDate();
		validateDateRange(startDate, endDate);

		habit.update(
			command.areaId(), normalize(command.name()), normalize(command.description()),
			command.measurementType(), command.targetValue(), normalize(command.unit()),
			command.dayPeriod(), command.preferredTime(), command.position(), command.startDate(), command.endDate(),
			command.scheduledDays(), command.active()
		);
		if (command.dayPeriods() != null) habit.setDayPeriods(command.dayPeriods());
		return repository.save(habit);
	}

	@Transactional
	public void archive(AppUser user, UUID habitId) {
		getOwnedActive(user.getId(), habitId).archive(Instant.now(clock));
	}

    @Transactional
    public void archivePeriod(AppUser user, UUID habitId, DayPeriod period) {
        var habit = getOwnedActive(user.getId(), habitId);
        if (period == null) { habit.archive(Instant.now(clock)); return; }
        var remaining = new java.util.HashSet<>(habit.getDayPeriods());
        if (!remaining.remove(period)) return;
        if (remaining.isEmpty()) habit.archive(Instant.now(clock));
        else habit.setDayPeriods(remaining);
        repository.save(habit);
    }

    @Transactional
    public void setOccurrenceCanceled(AppUser user, UUID habitId, LocalDate date, DayPeriod period, boolean canceled) {
        var habit = getOwnedActive(user.getId(), habitId);

        if (date.isBefore(habit.getStartDate()) || (habit.getEndDate() != null && date.isAfter(habit.getEndDate()))
            || !habit.getScheduledDays().contains(date.getDayOfWeek())) throw new ApiException(ErrorCode.HABIT_NOT_SCHEDULED);
        if (!habit.getDayPeriods().contains(period)) throw new ApiException(ErrorCode.VALIDATION_ERROR, "Turno inválido.");
        if (canceled) habit.cancelOccurrence(date, period); else habit.restoreOccurrence(date, period);
        repository.save(habit);
    }

	@Transactional(readOnly = true)
	public Habit getOwnedActive(UUID userId, UUID habitId) {
		return repository.findByIdAndUserIdAndArchivedAtIsNull(habitId, userId)
			.orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Hábito não encontrado."));
	}

	@Transactional(readOnly = true)
	public List<Habit> findScheduledFor(AppUser user, LocalDate date) {
		return sortedHabits(user.getId()).stream().filter(habit -> habit.isScheduledFor(date)).toList();
	}

	@Transactional(readOnly = true)
	public List<Habit> findAllActive(AppUser user) {
		return sortedHabits(user.getId());
	}

	private List<Habit> sortedHabits(UUID userId) {
		return repository.findAllByUserIdAndArchivedAtIsNullOrderByCreatedAtAsc(userId).stream()
			.sorted(Comparator
				.comparingInt((Habit habit) -> periodOrder(habit.getDayPeriod()))
				.thenComparingInt(Habit::getPosition)
				.thenComparing(Habit::getPreferredTime, Comparator.nullsLast(Comparator.naturalOrder()))
				.thenComparing(Habit::getCreatedAt))
			.toList();
	}

	private int periodOrder(DayPeriod period) {
		return switch (period) {
			case MORNING -> 0;
			case AFTERNOON -> 1;
			case EVENING -> 2;
			case ANYTIME -> 3;
		};
	}

	private void validateDateRange(LocalDate startDate, LocalDate endDate) {
		if (endDate != null && endDate.isBefore(startDate)) {
			throw new ApiException(ErrorCode.INVALID_DATE_RANGE);
		}
	}

	private String normalize(String value) {
		return value == null ? null : value.trim();
	}
}

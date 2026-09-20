package com.gurizadadointerior.clih.habit.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HabitRepository {

	List<Habit> findAllByUserIdAndArchivedAtIsNullOrderByCreatedAtAsc(UUID userId);

	Optional<Habit> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId);

	int findMaxPositionByUserId(UUID userId);

	Habit save(Habit habit);
}

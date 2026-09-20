package com.gurizadadointerior.clih.completion.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HabitCompletionRepository {

	Optional<HabitCompletion> findByUserIdAndIdempotencyKey(UUID userId, String idempotencyKey);

	boolean existsByUserIdAndHabitIdAndActivityDate(UUID userId, UUID habitId, LocalDate activityDate);

	List<HabitCompletion> findAllByUserIdAndActivityDate(UUID userId, LocalDate activityDate);

	List<HabitCompletion> findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(
		UUID userId, LocalDate startDate, LocalDate endDate
	);

	List<LocalDate> findDistinctActivityDatesByUserId(UUID userId);

	HabitCompletion saveAndFlush(HabitCompletion completion);
}

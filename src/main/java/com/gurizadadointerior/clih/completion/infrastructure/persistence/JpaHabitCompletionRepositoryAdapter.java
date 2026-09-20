package com.gurizadadointerior.clih.completion.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gurizadadointerior.clih.completion.domain.HabitCompletion;
import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
class JpaHabitCompletionRepositoryAdapter implements HabitCompletionRepository {

	private final SpringDataHabitCompletionRepository repository;

	JpaHabitCompletionRepositoryAdapter(SpringDataHabitCompletionRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<HabitCompletion> findByUserIdAndIdempotencyKey(UUID userId, String key) {
		return repository.findByUserIdAndIdempotencyKey(userId, key);
	}

	@Override
	public boolean existsByUserIdAndHabitIdAndActivityDate(UUID userId, UUID habitId, LocalDate date) {
		return repository.existsByUserIdAndHabitIdAndActivityDate(userId, habitId, date);
	}

	@Override
	public List<HabitCompletion> findAllByUserIdAndActivityDate(UUID userId, LocalDate date) {
		return repository.findAllByUserIdAndActivityDate(userId, date);
	}

	@Override
	public List<HabitCompletion> findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(UUID userId, LocalDate start, LocalDate end) {
		return repository.findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(userId, start, end);
	}

	@Override
	public List<LocalDate> findDistinctActivityDatesByUserId(UUID userId) {
		return repository.findDistinctActivityDatesByUserId(userId);
	}

	@Override
	public HabitCompletion saveAndFlush(HabitCompletion completion) {
		return repository.saveAndFlush(completion);
	}
}

interface SpringDataHabitCompletionRepository extends JpaRepository<HabitCompletion, UUID> {
	Optional<HabitCompletion> findByUserIdAndIdempotencyKey(UUID userId, String key);
	boolean existsByUserIdAndHabitIdAndActivityDate(UUID userId, UUID habitId, LocalDate date);
	List<HabitCompletion> findAllByUserIdAndActivityDate(UUID userId, LocalDate date);
	List<HabitCompletion> findAllByUserIdAndActivityDateBetweenOrderByActivityDateAsc(UUID userId, LocalDate start, LocalDate end);

	@Query("select distinct completion.activityDate from HabitCompletion completion where completion.userId = :userId order by completion.activityDate desc")
	List<LocalDate> findDistinctActivityDatesByUserId(@Param("userId") UUID userId);
}

package com.gurizadadointerior.clih.habit.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gurizadadointerior.clih.habit.domain.Habit;
import com.gurizadadointerior.clih.habit.domain.HabitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
class JpaHabitRepositoryAdapter implements HabitRepository {

	private final SpringDataHabitRepository repository;

	JpaHabitRepositoryAdapter(SpringDataHabitRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Habit> findAllByUserIdAndArchivedAtIsNullOrderByCreatedAtAsc(UUID userId) {
		return repository.findAllByUserIdAndArchivedAtIsNullOrderByCreatedAtAsc(userId);
	}

	@Override
	public Optional<Habit> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId) {
		return repository.findByIdAndUserIdAndArchivedAtIsNull(id, userId);
	}

	@Override
	public int findMaxPositionByUserId(UUID userId) {
		return repository.findMaxPositionByUserId(userId);
	}

	@Override
	public Habit save(Habit habit) {
		return repository.save(habit);
	}
}

interface SpringDataHabitRepository extends JpaRepository<Habit, UUID> {
	List<Habit> findAllByUserIdAndArchivedAtIsNullOrderByCreatedAtAsc(UUID userId);
	Optional<Habit> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId);

	@Query("select coalesce(max(habit.position), 0) from Habit habit where habit.userId = :userId")
	int findMaxPositionByUserId(@Param("userId") UUID userId);
}

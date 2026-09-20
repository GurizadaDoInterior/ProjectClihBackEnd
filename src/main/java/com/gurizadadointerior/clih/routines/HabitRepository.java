package com.gurizadadointerior.clih.routines;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HabitRepository extends JpaRepository<Habit, UUID> {

    @EntityGraph(attributePaths = "area")
    List<Habit> findByUserIdAndActiveTrueOrderByPosition(UUID userId);
}

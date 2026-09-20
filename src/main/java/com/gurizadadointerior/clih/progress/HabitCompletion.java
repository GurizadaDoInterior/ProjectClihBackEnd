package com.gurizadadointerior.clih.progress;

import com.gurizadadointerior.clih.routines.Habit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "habit_completions")
public class HabitCompletion {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    @Column(name = "recorded_value")
    private BigDecimal recordedValue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected HabitCompletion() {
    }

    public HabitCompletion(UUID userId, Habit habit, String idempotencyKey, Instant completedAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.habit = habit;
        this.idempotencyKey = idempotencyKey;
        this.completedAt = completedAt;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Habit getHabit() {
        return habit;
    }
}

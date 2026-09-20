package com.gurizadadointerior.clih.rewards;

import com.gurizadadointerior.clih.progress.HabitCompletion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "point_transactions")
public class PointTransaction {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completion_id")
    private HabitCompletion completion;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String reason;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PointTransaction() {
    }

    public PointTransaction(UUID userId, HabitCompletion completion, int amount, int balanceAfter) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.completion = completion;
        this.amount = amount;
        this.reason = "HABIT_COMPLETION";
        this.balanceAfter = balanceAfter;
        this.createdAt = Instant.now();
    }
}

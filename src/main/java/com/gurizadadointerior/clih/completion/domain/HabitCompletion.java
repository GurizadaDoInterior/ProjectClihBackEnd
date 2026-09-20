package com.gurizadadointerior.clih.completion.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.gurizadadointerior.clih.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "habit_completions")
public class HabitCompletion extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "habit_id", nullable = false)
	private UUID habitId;

	@Column(name = "activity_date", nullable = false)
	private LocalDate activityDate;

	@Column(name = "completed_at", nullable = false)
	private Instant completedAt;

	@Column(name = "recorded_value", nullable = false, precision = 12, scale = 2)
	private BigDecimal achievedValue;

	@Column(name = "idempotency_key", nullable = false, length = 120)
	private String idempotencyKey;

	@Column(length = 500)
	private String note;

	@Column(name = "xp_awarded", nullable = false)
	private int xpAwarded;

	@Version
	@Column(nullable = false)
	private long version;

	protected HabitCompletion() {
	}

	public HabitCompletion(
		UUID userId,
		UUID habitId,
		LocalDate activityDate,
		Instant completedAt,
		BigDecimal achievedValue,
		String idempotencyKey,
		String note
	) {
		this.userId = userId;
		this.habitId = habitId;
		this.activityDate = activityDate;
		this.completedAt = completedAt;
		this.achievedValue = achievedValue;
		this.idempotencyKey = idempotencyKey;
		this.note = note;
	}

	public void markXpAwarded(int xpAwarded) {
		this.xpAwarded = xpAwarded;
	}

	public UUID getUserId() { return userId; }
	public UUID getHabitId() { return habitId; }
	public LocalDate getActivityDate() { return activityDate; }
	public Instant getCompletedAt() { return completedAt; }
	public BigDecimal getAchievedValue() { return achievedValue; }
	public String getIdempotencyKey() { return idempotencyKey; }
	public String getNote() { return note; }
	public int getXpAwarded() { return xpAwarded; }
}

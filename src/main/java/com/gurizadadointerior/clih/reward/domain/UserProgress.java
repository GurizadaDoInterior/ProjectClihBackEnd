package com.gurizadadointerior.clih.reward.domain;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "user_progress")
public class UserProgress {

	@Id
	@Column(name = "user_id", nullable = false, updatable = false)
	private UUID userId;

	@Column(name = "total_xp", nullable = false)
	private long totalXp;

	@Column(name = "current_level", nullable = false)
	private int currentLevel;

	@Column(name = "current_streak", nullable = false)
	private int currentStreak;

	@Column(name = "longest_streak", nullable = false)
	private int longestStreak;

	@Column(name = "last_active_date")
	private LocalDate lastActiveDate;

	@Version
	@Column(nullable = false)
	private long version;

	protected UserProgress() {
	}

	public UserProgress(UUID userId) {
		this.userId = userId;
		this.currentLevel = 1;
	}

	public void applyExperience(int amount, int level, ProgressSnapshot streak) {
		this.totalXp += amount;
		this.currentLevel = level;
		this.currentStreak = streak.currentStreak();
		this.longestStreak = Math.max(this.longestStreak, streak.longestStreak());
		this.lastActiveDate = streak.lastActiveDate();
	}

	public UUID getUserId() { return userId; }
	public long getTotalXp() { return totalXp; }
	public int getCurrentLevel() { return currentLevel; }
	public int getCurrentStreak() { return currentStreak; }
	public int getLongestStreak() { return longestStreak; }
	public LocalDate getLastActiveDate() { return lastActiveDate; }
}

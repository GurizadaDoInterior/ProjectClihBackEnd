package com.gurizadadointerior.clih.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String timezone;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int xp;

    @Column(name = "streak_days", nullable = false)
    private int streakDays;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected User() {
    }

    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public int addXp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("A quantidade de XP não pode ser negativa.");
        }
        xp += amount;
        return xp;
    }
}

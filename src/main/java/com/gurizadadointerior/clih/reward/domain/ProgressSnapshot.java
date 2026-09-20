package com.gurizadadointerior.clih.reward.domain;

import java.time.LocalDate;

public record ProgressSnapshot(int currentStreak, int longestStreak, LocalDate lastActiveDate) {
}

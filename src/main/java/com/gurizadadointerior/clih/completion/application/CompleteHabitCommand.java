package com.gurizadadointerior.clih.completion.application;

import com.gurizadadointerior.clih.habit.domain.DayPeriod;

import java.math.BigDecimal;
import java.time.Instant;

public record CompleteHabitCommand(BigDecimal achievedValue, Instant completedAt, String note, DayPeriod dayPeriod) {
    public CompleteHabitCommand(BigDecimal value, Instant at, String note) { this(value, at, note, null); }
}

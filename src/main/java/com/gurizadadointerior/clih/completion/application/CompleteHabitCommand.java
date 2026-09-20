package com.gurizadadointerior.clih.completion.application;

import java.math.BigDecimal;
import java.time.Instant;

public record CompleteHabitCommand(BigDecimal achievedValue, Instant completedAt, String note) {
}

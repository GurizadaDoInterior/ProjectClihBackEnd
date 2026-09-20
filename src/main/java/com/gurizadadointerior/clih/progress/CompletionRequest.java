package com.gurizadadointerior.clih.progress;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CompletionRequest(@NotNull Instant completedAt) {
}

package com.gurizadadointerior.clih.progress;

import java.util.UUID;

public record CompletionResponse(
        UUID completionId,
        int pointsAwarded,
        int totalXp,
        boolean duplicated
) {
}

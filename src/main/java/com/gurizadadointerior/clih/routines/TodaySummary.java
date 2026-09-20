package com.gurizadadointerior.clih.routines;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TodaySummary(
        String displayName,
        int streakDays,
        int level,
        int xp,
        int xpToNextLevel,
        List<WeekDay> week,
        Map<String, Double> areaBalance,
        List<Activity> activities
) {
    public record WeekDay(String label, String state) {
    }

    public record Activity(
            UUID id,
            String title,
            String target,
            String area,
            boolean completed
    ) {
    }
}

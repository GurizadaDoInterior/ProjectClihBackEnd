package com.gurizadadointerior.clih.reward.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StreakCalculator {

	public ProgressSnapshot calculate(Collection<LocalDate> activityDates, LocalDate today) {
		Set<Long> dates = new HashSet<>();
		long todayEpoch = today.toEpochDay();
		for (var date : activityDates) {
			if (!date.isAfter(today)) dates.add(date.toEpochDay());
		}

		if (dates.isEmpty()) {
			return new ProgressSnapshot(0, 0, null);
		}

		int longest = 0;
		long lastActive = Long.MIN_VALUE;
		for (long date : dates) {
			lastActive = Math.max(lastActive, date);
			// Only start at the beginning of each run: every date is visited once.
			if (!dates.contains(date - 1)) {
				int running = 1;
				while (dates.contains(date + running)) running++;
				longest = Math.max(longest, running);
			}
		}

		int current = 0;
		if (lastActive >= todayEpoch - 1) {
			while (dates.contains(lastActive - current)) current++;
		}

		return new ProgressSnapshot(current, longest, LocalDate.ofEpochDay(lastActive));
	}
}

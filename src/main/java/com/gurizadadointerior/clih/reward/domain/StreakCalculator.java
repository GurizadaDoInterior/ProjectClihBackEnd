package com.gurizadadointerior.clih.reward.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class StreakCalculator {

	public ProgressSnapshot calculate(Collection<LocalDate> activityDates, LocalDate today) {
		List<LocalDate> dates = activityDates.stream()
			.distinct()
			.sorted(Comparator.reverseOrder())
			.toList();

		if (dates.isEmpty()) {
			return new ProgressSnapshot(0, 0, null);
		}

		int longest = 1;
		int running = 1;
		for (int index = 1; index < dates.size(); index++) {
			if (dates.get(index).equals(dates.get(index - 1).minusDays(1))) {
				running++;
				longest = Math.max(longest, running);
			} else {
				running = 1;
			}
		}

		var lastActive = dates.getFirst();
		int current = 0;
		if (!lastActive.isBefore(today.minusDays(1))) {
			current = 1;
			for (int index = 1; index < dates.size(); index++) {
				if (!dates.get(index).equals(dates.get(index - 1).minusDays(1))) {
					break;
				}
				current++;
			}
		}

		return new ProgressSnapshot(current, longest, lastActive);
	}
}

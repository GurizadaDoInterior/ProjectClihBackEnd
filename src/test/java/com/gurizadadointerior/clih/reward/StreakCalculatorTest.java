package com.gurizadadointerior.clih.reward.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class StreakCalculatorTest {

	private final StreakCalculator calculator = new StreakCalculator();

	@Test
	void calculatesCurrentAndLongestStreaks() {
		var today = LocalDate.of(2026, 9, 19);
		var result = calculator.calculate(List.of(
			today,
			today.minusDays(1),
			today.minusDays(2),
			today.minusDays(5),
			today.minusDays(6),
			today.minusDays(7),
			today.minusDays(8)
		), today);

		assertThat(result.currentStreak()).isEqualTo(3);
		assertThat(result.longestStreak()).isEqualTo(4);
		assertThat(result.lastActiveDate()).isEqualTo(today);
	}

	@Test
	void expiresCurrentStreakAfterMissingYesterday() {
		var today = LocalDate.of(2026, 9, 19);
		var result = calculator.calculate(List.of(today.minusDays(2), today.minusDays(3)), today);

		assertThat(result.currentStreak()).isZero();
		assertThat(result.longestStreak()).isEqualTo(2);
	}
}

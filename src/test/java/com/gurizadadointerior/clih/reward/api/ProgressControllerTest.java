package com.gurizadadointerior.clih.reward.api;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.reward.application.ProgressView;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProgressControllerTest {

	@Test
	void expiresStaleStreakUsingConfirmedHistoryAndTheUsersTimezone() {
		var users = mock(CurrentUserProvider.class);
		var rewards = mock(RewardApplicationService.class);
		var completions = mock(HabitCompletionRepository.class);
		var user = mock(AppUser.class);
		var userId = UUID.randomUUID();
		when(users.getCurrentUser()).thenReturn(user);
		when(user.getId()).thenReturn(userId);
		when(user.zoneId()).thenReturn(ZoneId.of("Asia/Tokyo"));
		var lastActive = LocalDate.of(2026, 10, 18);
		when(rewards.getProgress(userId)).thenReturn(new ProgressView(250, 3, 200, 300, 3, 8, lastActive));
		when(completions.findDistinctActivityDatesByUserId(userId))
			.thenReturn(List.of(lastActive, lastActive.minusDays(1), lastActive.minusDays(2)));
		// It is already October 20 in Tokyo, so October 18 no longer preserves the streak.
		var clock = Clock.fixed(Instant.parse("2026-10-19T18:00:00Z"), ZoneOffset.UTC);
		var response = new ProgressController(users, rewards, completions, clock).getProgress();

		assertThat(response.currentStreak()).isZero();
		assertThat(response.longestStreak()).isEqualTo(3);
		assertThat(response.lastActiveDate()).isEqualTo(lastActive);
		assertThat(response.totalXp()).isEqualTo(250);
		assertThat(response.level()).isEqualTo(3);
	}
}

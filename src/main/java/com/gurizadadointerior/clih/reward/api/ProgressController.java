package com.gurizadadointerior.clih.reward.api;

import java.time.Clock;
import java.time.LocalDate;

import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.reward.application.ProgressView;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

	private final CurrentUserProvider currentUserProvider;
	private final RewardApplicationService rewardService;
	private final HabitCompletionRepository completionRepository;
	private final Clock clock;

	public ProgressController(CurrentUserProvider currentUserProvider, RewardApplicationService rewardService,
		HabitCompletionRepository completionRepository, Clock clock) {
		this.currentUserProvider = currentUserProvider;
		this.rewardService = rewardService;
		this.completionRepository = completionRepository;
		this.clock = clock;
	}

	@GetMapping
	ProgressView getProgress() {
		var user = currentUserProvider.getCurrentUser();
		return rewardService.getProgress(user.getId()).withHistory(
			completionRepository.findDistinctActivityDatesByUserId(user.getId()),
			LocalDate.now(clock.withZone(user.zoneId()))
		);
	}
}

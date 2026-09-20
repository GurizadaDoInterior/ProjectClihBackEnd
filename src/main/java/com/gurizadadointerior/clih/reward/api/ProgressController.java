package com.gurizadadointerior.clih.reward.api;

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

	public ProgressController(CurrentUserProvider currentUserProvider, RewardApplicationService rewardService) {
		this.currentUserProvider = currentUserProvider;
		this.rewardService = rewardService;
	}

	@GetMapping
	ProgressView getProgress() {
		var user = currentUserProvider.getCurrentUser();
		return rewardService.getProgress(user.getId());
	}
}

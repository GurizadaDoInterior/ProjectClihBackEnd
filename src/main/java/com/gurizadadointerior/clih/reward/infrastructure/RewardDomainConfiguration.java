package com.gurizadadointerior.clih.reward.infrastructure;

import com.gurizadadointerior.clih.reward.domain.LevelPolicy;
import com.gurizadadointerior.clih.reward.domain.RewardPolicy;
import com.gurizadadointerior.clih.reward.domain.StreakCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RewardDomainConfiguration {

	@Bean
	LevelPolicy levelPolicy() {
		return new LevelPolicy();
	}

	@Bean
	RewardPolicy rewardPolicy() {
		return new RewardPolicy();
	}

	@Bean
	StreakCalculator streakCalculator() {
		return new StreakCalculator();
	}
}

package com.gurizadadointerior.clih.reward.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LevelPolicyTest {

	private final LevelPolicy policy = new LevelPolicy();

	@Test
	void startsAtLevelOneAndAdvancesEveryHundredXp() {
		assertThat(policy.levelFor(0)).isEqualTo(1);
		assertThat(policy.levelFor(99)).isEqualTo(1);
		assertThat(policy.levelFor(100)).isEqualTo(2);
		assertThat(policy.levelFor(250)).isEqualTo(3);
	}
}

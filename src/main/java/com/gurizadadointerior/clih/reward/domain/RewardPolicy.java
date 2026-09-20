package com.gurizadadointerior.clih.reward.domain;

import com.gurizadadointerior.clih.habit.domain.Habit;

public class RewardPolicy {

	private static final int DEFAULT_COMPLETION_XP = 10;

	public int experienceFor(Habit habit) {
		return DEFAULT_COMPLETION_XP;
	}
}

package com.gurizadadointerior.clih.reward.domain;

public class LevelPolicy {

	private static final int XP_PER_LEVEL = 100;

	public int levelFor(long totalXp) {
		return Math.toIntExact(totalXp / XP_PER_LEVEL) + 1;
	}

	public long xpForLevel(int level) {
		return (long) Math.max(0, level - 1) * XP_PER_LEVEL;
	}
}

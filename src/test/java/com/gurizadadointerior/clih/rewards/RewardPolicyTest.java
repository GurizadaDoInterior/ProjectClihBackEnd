package com.gurizadadointerior.clih.rewards;

import com.gurizadadointerior.clih.routines.Habit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RewardPolicyTest {

    private final RewardPolicy rewardPolicy = new RewardPolicy();

    @Test
    void awardsTenPointsForAnActiveHabit() {
        var habit = mock(Habit.class);
        when(habit.isActive()).thenReturn(true);

        assertThat(rewardPolicy.pointsFor(habit)).isEqualTo(10);
    }
}

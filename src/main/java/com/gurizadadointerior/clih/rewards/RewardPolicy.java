package com.gurizadadointerior.clih.rewards;

import com.gurizadadointerior.clih.routines.Habit;
import org.springframework.stereotype.Component;

@Component
public class RewardPolicy {

    public int pointsFor(Habit habit) {
        if (!habit.isActive()) {
            throw new IllegalArgumentException("Uma atividade pausada não concede pontos.");
        }
        return 10;
    }
}

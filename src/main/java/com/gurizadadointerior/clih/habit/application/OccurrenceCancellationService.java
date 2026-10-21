package com.gurizadadointerior.clih.habit.application;

import java.time.LocalDate;
import java.util.UUID;
import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.completion.domain.HabitCompletionRepository;
import com.gurizadadointerior.clih.reward.application.RewardApplicationService;
import com.gurizadadointerior.clih.shared.error.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OccurrenceCancellationService {
    private final HabitApplicationService habits;
    private final HabitCompletionRepository completions;
    private final RewardApplicationService rewards;
    private final com.gurizadadointerior.clih.completion.application.CompletionApplicationService completionService;
    public OccurrenceCancellationService(HabitApplicationService habits, HabitCompletionRepository completions, RewardApplicationService rewards, com.gurizadadointerior.clih.completion.application.CompletionApplicationService completionService) {
        this.completionService=completionService; this.habits=habits; this.completions=completions; this.rewards=rewards;
    }
    @Transactional
    public void setCanceled(AppUser user, UUID habitId, LocalDate date, DayPeriod period, boolean canceled) {
        rewards.lockProgress(user.getId());
        habits.getOwnedActive(user.getId(), habitId);
        if (canceled) {
            completions.findAllByUserIdAndActivityDate(user.getId(),date).stream()
                .filter(c -> c.getHabitId().equals(habitId) && c.getDayPeriod() == period)
                .forEach(c -> completionService.undo(user,habitId,c.getId()));
        }
        habits.setOccurrenceCanceled(user, habitId, date, period, canceled);
    }
}

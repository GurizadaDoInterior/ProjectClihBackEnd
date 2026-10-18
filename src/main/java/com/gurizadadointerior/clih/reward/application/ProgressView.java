package com.gurizadadointerior.clih.reward.application;

import java.time.LocalDate;
import java.util.Collection;
import com.gurizadadointerior.clih.reward.domain.StreakCalculator;

public record ProgressView(
 long totalXp, int level, long xpForCurrentLevel, long xpForNextLevel,
 int currentStreak, int longestStreak, LocalDate lastActiveDate
) {
 public ProgressView withHistory(Collection<LocalDate> dates, LocalDate today) {
  var actual = new StreakCalculator().calculate(dates,today);
  return new ProgressView(totalXp,level,xpForCurrentLevel,xpForNextLevel,
   actual.currentStreak(),actual.longestStreak(),actual.lastActiveDate());
 }
}

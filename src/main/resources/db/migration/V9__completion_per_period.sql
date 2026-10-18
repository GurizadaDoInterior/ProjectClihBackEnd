-- Keep all habits, selected periods, completions and XP. Attribute legacy
-- completions to their original primary period; new completions specify a period.
ALTER TABLE habit_completions ADD COLUMN day_period VARCHAR(20);
UPDATE habit_completions c SET day_period = h.day_period FROM habits h WHERE h.id = c.habit_id;
ALTER TABLE habit_completions ALTER COLUMN day_period SET NOT NULL;
ALTER TABLE habit_completions ADD CONSTRAINT ck_completion_period CHECK(day_period IN ('MORNING','AFTERNOON','EVENING','ANYTIME'));
CREATE UNIQUE INDEX uq_active_habit_completion_per_period
 ON habit_completions(user_id, habit_id, activity_date, day_period) WHERE undone_at IS NULL;
DROP INDEX uq_active_habit_completion_per_day;

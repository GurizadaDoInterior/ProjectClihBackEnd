ALTER TABLE habit_completions ADD COLUMN undone_at TIMESTAMPTZ;
ALTER TABLE habit_completions DROP CONSTRAINT uq_habit_completion_per_day;
CREATE UNIQUE INDEX uq_active_habit_completion_per_day
  ON habit_completions(user_id, habit_id, activity_date) WHERE undone_at IS NULL;
ALTER TABLE point_transactions DROP CONSTRAINT point_transactions_completion_id_key;
ALTER TABLE point_transactions ADD CONSTRAINT uq_completion_xp_reason UNIQUE (completion_id, reason);

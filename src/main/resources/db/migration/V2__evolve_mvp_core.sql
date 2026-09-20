ALTER TABLE app_users
  ADD COLUMN auth_subject VARCHAR(200),
  ADD COLUMN username VARCHAR(30),
  ADD COLUMN avatar_url VARCHAR(500),
  ADD COLUMN timezone_id VARCHAR(60),
  ADD COLUMN profile_visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
  ADD COLUMN onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

UPDATE app_users
SET auth_subject = CASE
    WHEN id = '00000000-0000-0000-0000-000000000001' THEN 'local-development-user'
    ELSE 'legacy:' || id::text
  END,
  username = CASE
    WHEN id = '00000000-0000-0000-0000-000000000001' THEN 'gustavo'
    ELSE NULL
  END,
  timezone_id = timezone,
  onboarding_completed = TRUE;

ALTER TABLE app_users
  ALTER COLUMN auth_subject SET NOT NULL,
  ALTER COLUMN timezone_id SET NOT NULL,
  ALTER COLUMN email DROP NOT NULL,
  ALTER COLUMN timezone DROP NOT NULL;

ALTER TABLE app_users
  ADD CONSTRAINT uq_app_users_auth_subject UNIQUE (auth_subject);

CREATE UNIQUE INDEX uq_app_users_username_lower
  ON app_users (LOWER(username))
  WHERE username IS NOT NULL;

ALTER TABLE areas
  ALTER COLUMN name TYPE VARCHAR(60),
  ALTER COLUMN icon TYPE VARCHAR(50),
  ADD COLUMN archived_at TIMESTAMPTZ,
  ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE UNIQUE INDEX uq_active_area_name_per_user
  ON areas (user_id, LOWER(name))
  WHERE archived_at IS NULL;

ALTER TABLE habits
  ALTER COLUMN name TYPE VARCHAR(100),
  ALTER COLUMN target_label DROP NOT NULL,
  ALTER COLUMN period DROP NOT NULL,
  ADD COLUMN description VARCHAR(500),
  ADD COLUMN measurement_type VARCHAR(30) NOT NULL DEFAULT 'COUNT',
  ADD COLUMN target_value NUMERIC(12, 2) NOT NULL DEFAULT 1,
  ADD COLUMN unit VARCHAR(30),
  ADD COLUMN day_period VARCHAR(20),
  ADD COLUMN preferred_time TIME,
  ADD COLUMN start_date DATE NOT NULL DEFAULT CURRENT_DATE,
  ADD COLUMN end_date DATE,
  ADD COLUMN archived_at TIMESTAMPTZ,
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE habits
SET day_period = period,
  measurement_type = CASE
    WHEN LOWER(target_label) LIKE '%min%' THEN 'MINUTES'
    WHEN LOWER(target_label) LIKE '%km%' THEN 'DISTANCE_KM'
    WHEN LOWER(target_label) LIKE '%p_gina%' THEN 'PAGES'
    ELSE 'COUNT'
  END,
  target_value = COALESCE(
    REPLACE(NULLIF(SUBSTRING(target_label FROM '([0-9]+([.,][0-9]+)?)'), ''), ',', '.')::NUMERIC,
    1
  ),
  unit = CASE
    WHEN LOWER(target_label) LIKE '%min%' THEN 'minutos'
    WHEN LOWER(target_label) LIKE '%km%' THEN 'km'
    WHEN LOWER(target_label) LIKE '%p_gina%' THEN 'páginas'
    WHEN LOWER(target_label) LIKE '%hora%' THEN 'horas'
    ELSE NULL
  END;

ALTER TABLE habits
  ALTER COLUMN day_period SET NOT NULL,
  ADD CONSTRAINT ck_habits_target_positive CHECK (target_value > 0),
  ADD CONSTRAINT ck_habits_date_range CHECK (end_date IS NULL OR end_date >= start_date);

CREATE TABLE habit_schedules (
  habit_id UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
  day_of_week VARCHAR(10) NOT NULL,
  PRIMARY KEY (habit_id, day_of_week)
);

INSERT INTO habit_schedules (habit_id, day_of_week)
SELECT habit.id, schedule.day_of_week
FROM habits habit
CROSS JOIN (VALUES
  ('MONDAY'), ('TUESDAY'), ('WEDNESDAY'), ('THURSDAY'),
  ('FRIDAY'), ('SATURDAY'), ('SUNDAY')
) AS schedule(day_of_week);

ALTER TABLE habit_completions
  ADD COLUMN activity_date DATE,
  ADD COLUMN note VARCHAR(500),
  ADD COLUMN xp_awarded INTEGER NOT NULL DEFAULT 10,
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE habit_completions completion
SET activity_date = completion.completed_at::date,
  recorded_value = habit.target_value
FROM habits habit
WHERE habit.id = completion.habit_id
  AND completion.recorded_value IS NULL;

ALTER TABLE habit_completions
  ALTER COLUMN activity_date SET NOT NULL,
  ALTER COLUMN recorded_value SET NOT NULL,
  ADD CONSTRAINT uq_habit_completion_per_day UNIQUE (user_id, habit_id, activity_date),
  ADD CONSTRAINT ck_completion_value_positive CHECK (recorded_value > 0),
  ADD CONSTRAINT ck_completion_xp_non_negative CHECK (xp_awarded >= 0);

CREATE INDEX idx_completions_user_activity_date
  ON habit_completions(user_id, activity_date);

CREATE TABLE user_progress (
  user_id UUID PRIMARY KEY REFERENCES app_users(id),
  total_xp BIGINT NOT NULL DEFAULT 0,
  current_level INTEGER NOT NULL DEFAULT 1,
  current_streak INTEGER NOT NULL DEFAULT 0,
  longest_streak INTEGER NOT NULL DEFAULT 0,
  last_active_date DATE,
  version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT ck_progress_xp_non_negative CHECK (total_xp >= 0),
  CONSTRAINT ck_progress_level_positive CHECK (current_level >= 1)
);

INSERT INTO user_progress (
  user_id, total_xp, current_level, current_streak, longest_streak, last_active_date
)
SELECT
  app_user.id,
  COUNT(completion.id) * 10,
  FLOOR((COUNT(completion.id) * 10) / 100.0)::INTEGER + 1,
  CASE WHEN COUNT(completion.id) > 0 THEN 1 ELSE 0 END,
  CASE WHEN COUNT(completion.id) > 0 THEN 1 ELSE 0 END,
  MAX(completion.activity_date)
FROM app_users app_user
LEFT JOIN habit_completions completion ON completion.user_id = app_user.id
GROUP BY app_user.id;

ALTER TABLE point_transactions
  ALTER COLUMN balance_after TYPE BIGINT,
  ALTER COLUMN reason TYPE VARCHAR(30),
  ADD COLUMN description VARCHAR(200),
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

WITH ordered_completions AS (
  SELECT
    completion.*,
    ROW_NUMBER() OVER (
      PARTITION BY completion.user_id
      ORDER BY completion.completed_at, completion.id
    ) AS sequence
  FROM habit_completions completion
)
INSERT INTO point_transactions (
  id, user_id, completion_id, amount, reason, balance_after, description, created_at, updated_at
)
SELECT
  gen_random_uuid(),
  completion.user_id,
  completion.id,
  completion.xp_awarded,
  'HABIT_COMPLETION',
  completion.sequence * completion.xp_awarded,
  'Conclusão migrada',
  completion.created_at,
  completion.updated_at
FROM ordered_completions completion
WHERE NOT EXISTS (
  SELECT 1 FROM point_transactions transaction WHERE transaction.completion_id = completion.id
);

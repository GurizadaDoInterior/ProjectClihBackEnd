-- Tenant ownership is enforced in the database as well as in application queries.
-- These composite foreign keys never cascade deletes; lifecycle remains explicit.

ALTER TABLE areas
  ADD COLUMN version BIGINT NOT NULL DEFAULT 0,
  ADD CONSTRAINT uq_areas_id_user UNIQUE (id, user_id);

ALTER TABLE habits
  ADD COLUMN version BIGINT NOT NULL DEFAULT 0,
  ADD CONSTRAINT uq_habits_id_user UNIQUE (id, user_id),
  ADD CONSTRAINT fk_habits_area_owner
    FOREIGN KEY (area_id, user_id) REFERENCES areas (id, user_id);

ALTER TABLE habit_completions
  ADD COLUMN version BIGINT NOT NULL DEFAULT 0,
  ADD CONSTRAINT uq_completions_id_user UNIQUE (id, user_id),
  ADD CONSTRAINT fk_completions_habit_owner
    FOREIGN KEY (habit_id, user_id) REFERENCES habits (id, user_id);

ALTER TABLE point_transactions
  ADD CONSTRAINT fk_point_transactions_completion_owner
    FOREIGN KEY (completion_id, user_id) REFERENCES habit_completions (id, user_id);

ALTER TABLE app_users
  ADD CONSTRAINT ck_app_users_profile_visibility
    CHECK (profile_visibility IN ('PRIVATE', 'FOLLOWERS', 'PUBLIC'));

ALTER TABLE habits
  ADD CONSTRAINT ck_habits_measurement_type
    CHECK (measurement_type IN ('BOOLEAN', 'COUNT', 'MINUTES', 'PAGES', 'DISTANCE_KM', 'MONEY')),
  ADD CONSTRAINT ck_habits_day_period
    CHECK (day_period IN ('MORNING', 'AFTERNOON', 'EVENING', 'ANYTIME')),
  ADD CONSTRAINT ck_habits_position_non_negative CHECK (position >= 0);

ALTER TABLE habit_schedules
  ADD CONSTRAINT ck_habit_schedules_day
    CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'));

ALTER TABLE areas
  ADD CONSTRAINT ck_areas_position_non_negative CHECK (position >= 0),
  ADD CONSTRAINT ck_areas_color_hex CHECK (color ~ '^#[0-9A-F]{6}$');

CREATE TABLE habit_day_periods (
 habit_id UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
 day_period VARCHAR(20) NOT NULL CHECK (day_period IN ('MORNING','AFTERNOON','EVENING','ANYTIME')),
 PRIMARY KEY (habit_id, day_period)
);
INSERT INTO habit_day_periods(habit_id,day_period) SELECT id,day_period FROM habits;

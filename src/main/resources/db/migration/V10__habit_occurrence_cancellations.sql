CREATE TABLE habit_canceled_occurrences (
    habit_id UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
    occurrence VARCHAR(40) NOT NULL,
    PRIMARY KEY (habit_id, occurrence)
);

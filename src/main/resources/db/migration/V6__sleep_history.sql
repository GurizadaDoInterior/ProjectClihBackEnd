CREATE TABLE sleep_entries (
 user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
 sleep_date DATE NOT NULL,
 slept_at VARCHAR(5) NOT NULL,
 woke_at VARCHAR(5) NOT NULL,
 quality VARCHAR(10) NOT NULL CHECK (quality IN ('Ruim','Regular','Boa','Ótima')),
 duration_minutes INTEGER NOT NULL CHECK (duration_minutes BETWEEN 1 AND 1439),
 PRIMARY KEY(user_id,sleep_date)
);

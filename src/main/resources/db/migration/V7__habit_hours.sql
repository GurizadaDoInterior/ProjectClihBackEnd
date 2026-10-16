ALTER TABLE habits DROP CONSTRAINT ck_habits_measurement_type;
ALTER TABLE habits ADD CONSTRAINT ck_habits_measurement_type
  CHECK (measurement_type IN ('BOOLEAN','COUNT','MINUTES','HOURS','PAGES','DISTANCE_KM','MONEY'));

package com.gurizadadointerior.clih.sleep.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SleepRecordRepository {

	List<SleepRecord> findAllByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to);

	Optional<SleepRecord> findByUserIdAndDate(UUID userId, LocalDate date);

	void save(SleepRecord record);

	void deleteByUserIdAndDate(UUID userId, LocalDate date);
}

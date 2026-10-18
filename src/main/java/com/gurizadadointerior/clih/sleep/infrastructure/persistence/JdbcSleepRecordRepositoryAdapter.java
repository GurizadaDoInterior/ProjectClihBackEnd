package com.gurizadadointerior.clih.sleep.infrastructure.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gurizadadointerior.clih.sleep.domain.SleepQuality;
import com.gurizadadointerior.clih.sleep.domain.SleepRecord;
import com.gurizadadointerior.clih.sleep.domain.SleepRecordRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class JdbcSleepRecordRepositoryAdapter implements SleepRecordRepository {

	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
	private static final String SELECT_COLUMNS =
		"SELECT user_id, sleep_date, slept_at, woke_at, quality FROM sleep_entries ";

	private final JdbcTemplate jdbc;

	JdbcSleepRecordRepositoryAdapter(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public List<SleepRecord> findAllByUserIdAndDateBetween(UUID userId, LocalDate from, LocalDate to) {
		return jdbc.query(
			SELECT_COLUMNS + "WHERE user_id = ? AND sleep_date BETWEEN ? AND ? ORDER BY sleep_date",
			this::map,
			userId,
			from,
			to
		);
	}

	@Override
	public Optional<SleepRecord> findByUserIdAndDate(UUID userId, LocalDate date) {
		return jdbc.query(
			SELECT_COLUMNS + "WHERE user_id = ? AND sleep_date = ?",
			this::map,
			userId,
			date
		).stream().findFirst();
	}

	@Override
	public void save(SleepRecord record) {
		jdbc.update(
			"""
				INSERT INTO sleep_entries(user_id, sleep_date, slept_at, woke_at, quality, duration_minutes)
				VALUES (?, ?, ?, ?, ?, ?)
				ON CONFLICT(user_id, sleep_date) DO UPDATE SET
					slept_at = EXCLUDED.slept_at,
					woke_at = EXCLUDED.woke_at,
					quality = EXCLUDED.quality,
					duration_minutes = EXCLUDED.duration_minutes
				""",
			record.getUserId(),
			record.getDate(),
			record.getSleptAt().format(TIME_FORMAT),
			record.getWokeAt().format(TIME_FORMAT),
			record.getQuality().label(),
			record.durationMinutes()
		);
	}

	@Override
	public void deleteByUserIdAndDate(UUID userId, LocalDate date) {
		jdbc.update("DELETE FROM sleep_entries WHERE user_id = ? AND sleep_date = ?", userId, date);
	}

	private SleepRecord map(ResultSet resultSet, int rowNumber) throws SQLException {
		return new SleepRecord(
			resultSet.getObject("user_id", UUID.class),
			resultSet.getObject("sleep_date", LocalDate.class),
			LocalTime.parse(resultSet.getString("slept_at"), TIME_FORMAT),
			LocalTime.parse(resultSet.getString("woke_at"), TIME_FORMAT),
			SleepQuality.fromExternalValue(resultSet.getString("quality"))
		);
	}
}

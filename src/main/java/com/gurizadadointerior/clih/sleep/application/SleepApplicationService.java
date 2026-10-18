package com.gurizadadointerior.clih.sleep.application;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.sleep.domain.SleepRecord;
import com.gurizadadointerior.clih.sleep.domain.SleepRecordRepository;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SleepApplicationService {

	private static final long MAXIMUM_HISTORY_RANGE_DAYS = 366;

	private final SleepRecordRepository repository;
	private final Clock clock;

	public SleepApplicationService(SleepRecordRepository repository, Clock clock) {
		this.repository = repository;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public List<SleepRecord> list(AppUser user, LocalDate from, LocalDate to) {
		validateRange(from, to);
		return repository.findAllByUserIdAndDateBetween(user.getId(), from, to);
	}

	@Transactional(readOnly = true)
	public SleepRecord get(AppUser user, LocalDate date) {
		return repository.findByUserIdAndDate(user.getId(), date)
			.orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Registro de sono não encontrado."));
	}

	@Transactional
	public SleepRecord save(AppUser user, SaveSleepCommand command) {
		var today = LocalDate.now(clock.withZone(user.zoneId()));
		if (command.date().isAfter(today)) {
			throw new ApiException(ErrorCode.VALIDATION_ERROR, "A data do sono não pode estar no futuro.");
		}

		final SleepRecord record;
		try {
			record = new SleepRecord(
				user.getId(), command.date(), command.sleptAt(), command.wokeAt(), command.quality()
			);
		} catch (IllegalArgumentException exception) {
			throw new ApiException(ErrorCode.VALIDATION_ERROR, exception.getMessage());
		}

		repository.save(record);
		return record;
	}

	@Transactional
	public void delete(AppUser user, LocalDate date) {
		repository.deleteByUserIdAndDate(user.getId(), date);
	}

	private void validateRange(LocalDate from, LocalDate to) {
		if (to.isBefore(from) || ChronoUnit.DAYS.between(from, to) > MAXIMUM_HISTORY_RANGE_DAYS) {
			throw new ApiException(ErrorCode.INVALID_DATE_RANGE);
		}
	}
}

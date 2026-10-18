package com.gurizadadointerior.clih.sleep.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.sleep.application.SaveSleepCommand;
import com.gurizadadointerior.clih.sleep.domain.SleepQuality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SaveSleepRequest(
	@NotBlank
	@Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Use o formato HH:mm.")
	String sleptAt,
	@NotBlank
	@Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Use o formato HH:mm.")
	String wokeAt,
	@NotBlank String quality
) {
	public SaveSleepCommand toCommand(LocalDate date) {
		try {
			return new SaveSleepCommand(
				date,
				LocalTime.parse(sleptAt),
				LocalTime.parse(wokeAt),
				SleepQuality.fromExternalValue(quality)
			);
		} catch (IllegalArgumentException exception) {
			throw new ApiException(ErrorCode.VALIDATION_ERROR, exception.getMessage());
		}
	}
}

package com.gurizadadointerior.clih.completion.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.gurizadadointerior.clih.completion.application.CompleteHabitCommand;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Digits;

public record CreateCompletionRequest(
	@Positive(message = "deve ser maior que zero")
	@Digits(integer = 10, fraction = 2, message = "deve ter até 10 inteiros e 2 casas decimais") BigDecimal achievedValue,
	Instant completedAt,
	@Size(max = 500, message = "deve ter no máximo 500 caracteres") String note
) {
	public CompleteHabitCommand toCommand() {
		return new CompleteHabitCommand(achievedValue, completedAt, note);
	}
}

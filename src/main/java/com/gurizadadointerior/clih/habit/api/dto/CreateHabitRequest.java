package com.gurizadadointerior.clih.habit.api.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.gurizadadointerior.clih.habit.application.CreateHabitCommand;
import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.habit.domain.MeasurementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateHabitRequest(
	@NotNull(message = "é obrigatória") UUID areaId,
	@NotBlank(message = "é obrigatório") @Size(max = 100) String name,
	@Size(max = 500) String description,
	@NotNull(message = "é obrigatório") MeasurementType measurementType,
	@NotNull(message = "é obrigatório") @Positive(message = "deve ser maior que zero")
	@Digits(integer = 10, fraction = 2, message = "deve ter até 10 inteiros e 2 casas decimais") BigDecimal targetValue,
	@Size(max = 30) String unit,
	@NotNull(message = "é obrigatório") DayPeriod dayPeriod,
	LocalTime preferredTime,
	LocalDate startDate,
	LocalDate endDate,
	@NotEmpty(message = "selecione ao menos um dia") Set<DayOfWeek> scheduledDays
) {
	public CreateHabitCommand toCommand() {
		return new CreateHabitCommand(
			areaId, name, description, measurementType, targetValue, unit, dayPeriod,
			preferredTime, startDate, endDate, scheduledDays
		);
	}
}

package com.gurizadadointerior.clih.habit.api.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import com.gurizadadointerior.clih.habit.application.UpdateHabitCommand;
import com.gurizadadointerior.clih.habit.domain.DayPeriod;
import com.gurizadadointerior.clih.habit.domain.MeasurementType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateHabitRequest(
	UUID areaId,
	@Size(min = 1, max = 100) @Pattern(regexp = ".*\\S.*", flags = Pattern.Flag.DOTALL, message = "não pode conter apenas espaços") String name,
	@Size(max = 500) String description,
	MeasurementType measurementType,
	@Positive(message = "deve ser maior que zero") @Digits(integer = 10, fraction = 2) BigDecimal targetValue,
	@Size(max = 30) String unit,
	DayPeriod dayPeriod,
	LocalTime preferredTime,
	@PositiveOrZero(message = "deve ser zero ou maior") @Max(value = 10000, message = "deve ser no máximo 10000") Integer position,
	LocalDate startDate,
	LocalDate endDate,
	@Size(min = 1, message = "selecione ao menos um dia") Set<DayOfWeek> scheduledDays,
	Boolean active
) {
	public UpdateHabitCommand toCommand() {
		return new UpdateHabitCommand(
			areaId, name, description, measurementType, targetValue, unit, dayPeriod,
			preferredTime, position, startDate, endDate, scheduledDays, active
		);
	}
}

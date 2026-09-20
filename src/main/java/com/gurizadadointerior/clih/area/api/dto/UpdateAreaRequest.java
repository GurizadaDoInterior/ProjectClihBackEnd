package com.gurizadadointerior.clih.area.api.dto;

import com.gurizadadointerior.clih.area.application.UpdateAreaCommand;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateAreaRequest(
	@Size(min = 1, max = 60, message = "deve ter entre 1 e 60 caracteres")
	@Pattern(regexp = ".*\\S.*", flags = Pattern.Flag.DOTALL, message = "não pode conter apenas espaços")
	String name,

	@Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "deve usar o formato hexadecimal #RRGGBB")
	String color,

	@Size(min = 1, max = 50, message = "deve ter entre 1 e 50 caracteres")
	@Pattern(regexp = ".*\\S.*", flags = Pattern.Flag.DOTALL, message = "não pode conter apenas espaços")
	String icon,

	@PositiveOrZero(message = "deve ser zero ou maior")
	@Max(value = 10000, message = "deve ser no máximo 10000")
	Integer position
) {
	public UpdateAreaCommand toCommand() {
		return new UpdateAreaCommand(name, color, icon, position);
	}
}

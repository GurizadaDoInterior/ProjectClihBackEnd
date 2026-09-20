package com.gurizadadointerior.clih.area.api.dto;

import com.gurizadadointerior.clih.area.application.CreateAreaCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAreaRequest(
	@NotBlank(message = "é obrigatório")
	@Size(max = 60, message = "deve ter no máximo 60 caracteres")
	String name,

	@NotBlank(message = "é obrigatória")
	@Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "deve usar o formato hexadecimal #RRGGBB")
	String color,

	@NotBlank(message = "é obrigatório")
	@Size(max = 50, message = "deve ter no máximo 50 caracteres")
	String icon
) {
	public CreateAreaCommand toCommand() {
		return new CreateAreaCommand(name, color, icon);
	}
}

package com.gurizadadointerior.clih.user.api.dto;

import com.gurizadadointerior.clih.user.application.UpdateUserCommand;
import com.gurizadadointerior.clih.user.domain.ProfileVisibility;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateMeRequest(
	@Pattern(regexp = "^[a-z0-9._]{3,30}$", message = "use de 3 a 30 caracteres: letras minúsculas, números, ponto ou sublinhado")
	String username,

	@Size(min = 2, max = 80, message = "deve ter entre 2 e 80 caracteres")
	@Pattern(regexp = ".*\\S.*", flags = Pattern.Flag.DOTALL, message = "não pode conter apenas espaços")
	String displayName,

	@Size(max = 500, message = "deve ter no máximo 500 caracteres")
	String avatarUrl,

	@Size(max = 60, message = "deve ter no máximo 60 caracteres")
	String timezoneId,

	ProfileVisibility profileVisibility,

	Boolean completeOnboarding
) {
	public UpdateUserCommand toCommand() {
		return new UpdateUserCommand(
			username, displayName, avatarUrl, timezoneId, profileVisibility, completeOnboarding
		);
	}
}

package com.gurizadadointerior.clih.user.application;

import com.gurizadadointerior.clih.user.domain.ProfileVisibility;

public record UpdateUserCommand(
	String username,
	String displayName,
	String avatarUrl,
	String timezoneId,
	ProfileVisibility profileVisibility,
	Boolean completeOnboarding
) {
}

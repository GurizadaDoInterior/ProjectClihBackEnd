package com.gurizadadointerior.clih.user.api.dto;

import java.time.Instant;
import java.util.UUID;

import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.user.domain.ProfileVisibility;

public record UserResponse(
	UUID id,
	String username,
	String displayName,
	String avatarUrl,
	String timezoneId,
	ProfileVisibility profileVisibility,
	boolean onboardingCompleted,
	Instant createdAt
) {
	public static UserResponse from(AppUser user) {
		return new UserResponse(
			user.getId(),
			user.getUsername(),
			user.getDisplayName(),
			user.getAvatarUrl(),
			user.getTimezoneId(),
			user.getProfileVisibility(),
			user.isOnboardingCompleted(),
			user.getCreatedAt()
		);
	}
}

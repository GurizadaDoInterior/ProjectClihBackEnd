package com.gurizadadointerior.clih.user.domain;

import java.time.ZoneId;

import com.gurizadadointerior.clih.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "app_users")
public class AppUser extends BaseEntity {

	@Column(name = "auth_subject", nullable = false, unique = true, length = 200)
	private String authSubject;

	@Column(unique = true, length = 30)
	private String username;

	@Column(name = "display_name", nullable = false, length = 80)
	private String displayName;

	@Column(name = "avatar_url", length = 500)
	private String avatarUrl;

	@Column(name = "timezone_id", nullable = false, length = 60)
	private String timezoneId;

	@Enumerated(EnumType.STRING)
	@Column(name = "profile_visibility", nullable = false, length = 20)
	private ProfileVisibility profileVisibility;

	@Column(name = "onboarding_completed", nullable = false)
	private boolean onboardingCompleted;

	@Version
	@Column(nullable = false)
	private long version;

	protected AppUser() {
	}

	public AppUser(String authSubject) {
		this.authSubject = authSubject;
		this.displayName = "Novo usuário";
		this.timezoneId = "UTC";
		this.profileVisibility = ProfileVisibility.PRIVATE;
	}

	public void updateProfile(
		String username,
		String displayName,
		String avatarUrl,
		String timezoneId,
		ProfileVisibility profileVisibility
	) {
		if (username != null) {
			this.username = username;
		}
		if (displayName != null) {
			this.displayName = displayName;
		}
		if (avatarUrl != null) {
			this.avatarUrl = avatarUrl;
		}
		if (timezoneId != null) {
			this.timezoneId = timezoneId;
		}
		if (profileVisibility != null) {
			this.profileVisibility = profileVisibility;
		}
	}

	public void completeOnboarding() {
		this.onboardingCompleted = true;
	}

	public ZoneId zoneId() {
		return ZoneId.of(timezoneId);
	}

	public String getAuthSubject() {
		return authSubject;
	}

	public String getUsername() {
		return username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public String getTimezoneId() {
		return timezoneId;
	}

	public ProfileVisibility getProfileVisibility() {
		return profileVisibility;
	}

	public boolean isOnboardingCompleted() {
		return onboardingCompleted;
	}
}

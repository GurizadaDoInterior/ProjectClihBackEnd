package com.gurizadadointerior.clih.user.application;

import java.time.DateTimeException;
import java.time.ZoneId;

import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.user.domain.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserApplicationService {

	private final AppUserRepository repository;

	public UserApplicationService(AppUserRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public AppUser getOrCreateBySubject(String subject) {
		return repository.findByAuthSubject(subject).orElseGet(() -> {
			repository.createIfAbsent(subject);
			return repository.findByAuthSubject(subject)
				.orElseThrow(() -> new IllegalStateException("Authenticated user could not be loaded"));
		});
	}

	@Transactional
	public AppUser update(AppUser user, UpdateUserCommand command) {
		var normalizedUsername = normalizeUsername(command.username());
		if (normalizedUsername != null && repository.existsByUsernameIgnoreCaseAndIdNot(normalizedUsername, user.getId())) {
			throw new ApiException(ErrorCode.USERNAME_ALREADY_TAKEN);
		}

		var timezoneId = validateTimezone(command.timezoneId());
		user.updateProfile(
			normalizedUsername,
			normalizeOptional(command.displayName()),
			validateAvatarUrl(command.avatarUrl()),
			timezoneId,
			command.profileVisibility()
		);
		if (Boolean.TRUE.equals(command.completeOnboarding())) {
			user.completeOnboarding();
		}
		return repository.save(user);
	}

	private String normalizeUsername(String value) {
		return value == null ? null : value.trim().toLowerCase();
	}

	private String normalizeOptional(String value) {
		return value == null ? null : value.trim();
	}

	private String validateTimezone(String value) {
		if (value == null) {
			return null;
		}
		try {
			return ZoneId.of(value.trim()).getId();
		} catch (DateTimeException exception) {
			throw new ApiException(ErrorCode.INVALID_TIMEZONE);
		}
	}

	private String validateAvatarUrl(String value) {
		var normalized = normalizeOptional(value);
		if (normalized == null || normalized.isEmpty()) {
			return normalized;
		}
		try {
			var uri = java.net.URI.create(normalized);
			if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
				throw new IllegalArgumentException();
			}
			return uri.toASCIIString();
		} catch (IllegalArgumentException exception) {
			throw new ApiException(ErrorCode.INVALID_AVATAR_URL);
		}
	}
}

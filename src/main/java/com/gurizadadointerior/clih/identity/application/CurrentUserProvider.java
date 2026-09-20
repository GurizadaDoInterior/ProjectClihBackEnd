package com.gurizadadointerior.clih.identity.application;

import com.gurizadadointerior.clih.shared.config.AppProperties;
import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.user.application.UserApplicationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

	private final AppProperties properties;
	private final UserApplicationService userService;

	public CurrentUserProvider(AppProperties properties, UserApplicationService userService) {
		this.properties = properties;
		this.userService = userService;
	}

	public AppUser getCurrentUser() {
		return userService.getOrCreateBySubject(resolveSubject());
	}

	private String resolveSubject() {
		if (properties.getSecurity().getMode() == AppProperties.SecurityMode.LOCAL) {
			return validatedSubject(properties.getSecurity().getLocalSubject());
		}

		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (properties.getSecurity().getMode() == AppProperties.SecurityMode.JWT
			&& authentication != null
			&& authentication.isAuthenticated()
			&& authentication.getPrincipal() instanceof Jwt jwt) {
			return validatedSubject(jwt.getSubject());
		}

		throw new ApiException(ErrorCode.AUTHENTICATION_REQUIRED);
	}

	private String validatedSubject(String subject) {
		if (subject == null || subject.isBlank() || subject.length() > 200) {
			throw new ApiException(ErrorCode.INVALID_AUTHENTICATION_SUBJECT);
		}
		return subject;
	}
}

package com.gurizadadointerior.clih.shared.config;

import java.net.URI;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
class SecurityStartupValidator implements SmartInitializingSingleton {

	private final AppProperties properties;
	private final Environment environment;

	SecurityStartupValidator(AppProperties properties, Environment environment) {
		this.properties = properties;
		this.environment = environment;
	}

	@Override
	public void afterSingletonsInstantiated() {
		var mode = properties.getSecurity().getMode();
		var localProfile = environment.acceptsProfiles(Profiles.of("local"));
		if (mode == AppProperties.SecurityMode.LOCAL && !localProfile) {
			throw new IllegalStateException("app.security.mode=local is only allowed with the local profile");
		}
		if (mode == AppProperties.SecurityMode.JWT
			&& isBlank(environment.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri"))) {
			throw new IllegalStateException("JWT issuer URI is required when JWT security is enabled");
		}
		if (mode == AppProperties.SecurityMode.LOCAL) {
			validateSubject(properties.getSecurity().getLocalSubject());
		}
		properties.getCors().getAllowedOrigins().forEach(origin -> validateOrigin(origin, localProfile));
	}

	private void validateSubject(String subject) {
		if (isBlank(subject) || subject.length() > 200) {
			throw new IllegalStateException("The local authentication subject must contain 1 to 200 characters");
		}
	}

	private void validateOrigin(String origin, boolean localProfile) {
		try {
			var uri = URI.create(origin);
			var localHttp = localProfile && "http".equalsIgnoreCase(uri.getScheme())
				&& ("localhost".equalsIgnoreCase(uri.getHost()) || "127.0.0.1".equals(uri.getHost()));
			if ("*".equals(origin) || uri.getHost() == null
				|| (!("https".equalsIgnoreCase(uri.getScheme()) || localHttp))) {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException exception) {
			throw new IllegalStateException("Invalid or insecure CORS origin: " + origin, exception);
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}

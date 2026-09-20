package com.gurizadadointerior.clih.shared.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class SecurityStartupValidatorTest {

	@Test
	void rejectsLocalSecurityOutsideLocalProfile() {
		var properties = new AppProperties();
		properties.getSecurity().setMode(AppProperties.SecurityMode.LOCAL);

		assertThatThrownBy(() -> new SecurityStartupValidator(properties, new MockEnvironment())
			.afterSingletonsInstantiated())
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("only allowed with the local profile");
	}

	@Test
	void acceptsLocalhostHttpOnlyInLocalProfile() {
		var properties = new AppProperties();
		properties.getSecurity().setMode(AppProperties.SecurityMode.LOCAL);
		properties.getCors().setAllowedOrigins(java.util.List.of("http://localhost:3000"));
		var environment = new MockEnvironment();
		environment.setActiveProfiles("local");

		assertThatCode(() -> new SecurityStartupValidator(properties, environment)
			.afterSingletonsInstantiated()).doesNotThrowAnyException();
	}

	@Test
	void rejectsWildcardCorsOrigin() {
		var properties = new AppProperties();
		properties.getCors().setAllowedOrigins(java.util.List.of("*"));

		assertThatThrownBy(() -> new SecurityStartupValidator(properties, new MockEnvironment())
			.afterSingletonsInstantiated())
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Invalid or insecure CORS origin");
	}
}

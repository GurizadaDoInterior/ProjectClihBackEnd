package com.gurizadadointerior.clih.shared.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(
		HttpSecurity http,
		AppProperties properties,
		SecurityErrorWriter errorWriter
	) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(Customizer.withDefaults())
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.requestCache(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint((request, response, exception) ->
					errorWriter.write(request, response, com.gurizadadointerior.clih.shared.error.ErrorCode.AUTHENTICATION_REQUIRED))
				.accessDeniedHandler((request, response, exception) ->
					errorWriter.write(request, response, com.gurizadadointerior.clih.shared.error.ErrorCode.ACCESS_DENIED)))
			.headers(headers -> headers
				.frameOptions(frame -> frame.deny())
				.referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
				.permissionsPolicyHeader(permissions ->
					permissions.policy("camera=(), geolocation=(), microphone=()")))
			.authorizeHttpRequests(authorize -> {
				authorize.requestMatchers("/actuator/health/**").permitAll();

				switch (properties.getSecurity().getMode()) {
					case LOCAL -> authorize.anyRequest().permitAll();
					case JWT -> authorize.anyRequest().authenticated();
					case DENY_ALL -> authorize.anyRequest().denyAll();
				}
			});

		if (properties.getSecurity().getMode() == AppProperties.SecurityMode.JWT) {
			http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
		}

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource(AppProperties properties) {
		var configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(properties.getCors().getAllowedOrigins());
		configuration.setAllowedMethods(List.of(
			HttpMethod.GET.name(),
			HttpMethod.POST.name(),
			HttpMethod.PUT.name(),
			HttpMethod.PATCH.name(),
			HttpMethod.DELETE.name(),
			HttpMethod.OPTIONS.name()
		));
		configuration.setAllowedHeaders(List.of(
			HttpHeaders.AUTHORIZATION,
			HttpHeaders.CONTENT_TYPE,
			"Idempotency-Key"
		));
		configuration.setExposedHeaders(List.of("Location"));
		configuration.setAllowCredentials(false);
		configuration.setMaxAge(3600L);

		var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}

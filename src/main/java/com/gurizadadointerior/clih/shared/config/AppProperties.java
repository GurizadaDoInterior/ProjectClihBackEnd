package com.gurizadadointerior.clih.shared.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

	private final Security security = new Security();
	private final Cors cors = new Cors();

	public Security getSecurity() {
		return security;
	}

	public Cors getCors() {
		return cors;
	}

	public static class Security {
		private SecurityMode mode = SecurityMode.DENY_ALL;
		private String localSubject = "local-development-user";

		public SecurityMode getMode() {
			return mode;
		}

		public void setMode(SecurityMode mode) {
			this.mode = mode;
		}

		public String getLocalSubject() {
			return localSubject;
		}

		public void setLocalSubject(String localSubject) {
			this.localSubject = localSubject;
		}
	}

	public static class Cors {
		private List<String> allowedOrigins = new ArrayList<>();

		public List<String> getAllowedOrigins() {
			return allowedOrigins;
		}

		public void setAllowedOrigins(List<String> allowedOrigins) {
			this.allowedOrigins = allowedOrigins;
		}
	}

	public enum SecurityMode {
		LOCAL,
		JWT,
		DENY_ALL
	}
}

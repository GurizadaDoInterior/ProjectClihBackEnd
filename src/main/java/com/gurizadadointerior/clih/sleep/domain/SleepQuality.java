package com.gurizadadointerior.clih.sleep.domain;

import java.text.Normalizer;
import java.util.Locale;

public enum SleepQuality {
	POOR("Ruim"),
	FAIR("Regular"),
	GOOD("Boa"),
	EXCELLENT("Ótima");

	private final String label;

	SleepQuality(String label) {
		this.label = label;
	}

	public String label() {
		return label;
	}

	public static SleepQuality fromExternalValue(String value) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Informe a qualidade do sono.");
		}

		var normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
			.replaceAll("\\p{M}", "")
			.toUpperCase(Locale.ROOT);

		return switch (normalized) {
			case "POOR", "RUIM" -> POOR;
			case "FAIR", "REGULAR" -> FAIR;
			case "GOOD", "BOA" -> GOOD;
			case "EXCELLENT", "OTIMA" -> EXCELLENT;
			default -> throw new IllegalArgumentException("Qualidade do sono inválida.");
		};
	}
}

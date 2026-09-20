package com.gurizadadointerior.clih.area.application;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

import com.gurizadadointerior.clih.area.domain.AreaRepository;
import org.springframework.stereotype.Component;

@Component
class SlugGenerator {

	private static final int MAX_LENGTH = 30;

	private final AreaRepository repository;

	SlugGenerator(AreaRepository repository) {
		this.repository = repository;
	}

	String uniqueFor(UUID userId, String name) {
		var base = normalize(name);
		var candidate = base;
		var suffix = 2;
		while (repository.existsByUserIdAndSlug(userId, candidate)) {
			var ending = "-" + suffix++;
			candidate = truncate(base, MAX_LENGTH - ending.length()) + ending;
		}
		return candidate;
	}

	private String normalize(String value) {
		var withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
			.replaceAll("\\p{M}", "");
		var slug = withoutAccents.toLowerCase(Locale.ROOT)
			.replaceAll("[^a-z0-9]+", "-")
			.replaceAll("(^-+|-+$)", "");
		return truncate(slug.isBlank() ? "area" : slug, MAX_LENGTH);
	}

	private String truncate(String value, int maxLength) {
		var truncated = value.substring(0, Math.min(value.length(), maxLength));
		return truncated.replaceAll("-+$", "");
	}
}

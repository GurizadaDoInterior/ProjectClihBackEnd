package com.gurizadadointerior.clih.area.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.gurizadadointerior.clih.area.domain.AreaRepository;
import org.junit.jupiter.api.Test;

class SlugGeneratorTest {

	private final AreaRepository repository = mock(AreaRepository.class);
	private final SlugGenerator generator = new SlugGenerator(repository);

	@Test
	void removesAccentsAndNormalizesTheName() {
		var userId = UUID.randomUUID();

		assertThat(generator.uniqueFor(userId, "Saúde e Bem-estar"))
			.isEqualTo("saude-e-bem-estar");
	}

	@Test
	void addsSuffixWhenSlugAlreadyExists() {
		var userId = UUID.randomUUID();
		when(repository.existsByUserIdAndSlug(userId, "estudos")).thenReturn(true);
		when(repository.existsByUserIdAndSlug(userId, "estudos-2")).thenReturn(false);

		assertThat(generator.uniqueFor(userId, "Estudos")).isEqualTo("estudos-2");
	}
}

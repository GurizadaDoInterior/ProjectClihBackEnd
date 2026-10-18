package com.gurizadadointerior.clih.evolution.api;

import java.time.LocalDate;
import java.time.YearMonth;

import com.gurizadadointerior.clih.evolution.application.EvolutionQueryService;
import com.gurizadadointerior.clih.evolution.application.view.EvolutionResponse;
import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evolution")
public class EvolutionController {

	private final CurrentUserProvider currentUserProvider;
	private final EvolutionQueryService evolutionService;

	public EvolutionController(CurrentUserProvider currentUserProvider, EvolutionQueryService evolutionService) {
		this.currentUserProvider = currentUserProvider;
		this.evolutionService = evolutionService;
	}

	@GetMapping
	EvolutionResponse getEvolution(
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate
	) {
		return evolutionService.getEvolution(currentUserProvider.getCurrentUser(), month, selectedDate);
	}
}

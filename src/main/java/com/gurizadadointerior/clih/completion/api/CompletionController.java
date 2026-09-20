package com.gurizadadointerior.clih.completion.api;

import java.util.UUID;

import com.gurizadadointerior.clih.completion.application.CompletionApplicationService;
import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.completion.api.dto.CompletionResponse;
import com.gurizadadointerior.clih.completion.api.dto.CreateCompletionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@Validated
@RequestMapping("/api/v1/habits")
public class CompletionController {

	private final CurrentUserProvider currentUserProvider;
	private final CompletionApplicationService completionService;

	public CompletionController(CurrentUserProvider currentUserProvider, CompletionApplicationService completionService) {
		this.currentUserProvider = currentUserProvider;
		this.completionService = completionService;
	}

	@PostMapping("/{habitId}/completions")
	ResponseEntity<CompletionResponse> complete(
		@PathVariable UUID habitId,
		@RequestHeader("Idempotency-Key")
		@NotBlank(message = "é obrigatória")
		@Size(max = 120, message = "deve ter no máximo 120 caracteres")
		@Pattern(regexp = "^[A-Za-z0-9._:-]+$", message = "deve conter apenas letras, números, ponto, hífen, sublinhado ou dois-pontos")
		String idempotencyKey,
		@Valid @RequestBody CreateCompletionRequest request
	) {
		var result = completionService.complete(
			currentUserProvider.getCurrentUser(), habitId, idempotencyKey, request.toCommand()
		);
		var response = CompletionResponse.from(result);
		return result.idempotentReplay() ? ResponseEntity.ok(response) : ResponseEntity.status(201).body(response);
	}
}

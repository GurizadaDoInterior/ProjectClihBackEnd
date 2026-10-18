package com.gurizadadointerior.clih.sleep.api;

import java.time.LocalDate;
import java.util.List;

import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.sleep.api.dto.SaveSleepRequest;
import com.gurizadadointerior.clih.sleep.api.dto.SleepResponse;
import com.gurizadadointerior.clih.sleep.application.SleepApplicationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sleep")
public class SleepController {

	private final CurrentUserProvider currentUserProvider;
	private final SleepApplicationService sleepService;

	public SleepController(CurrentUserProvider currentUserProvider, SleepApplicationService sleepService) {
		this.currentUserProvider = currentUserProvider;
		this.sleepService = sleepService;
	}

	@GetMapping
	List<SleepResponse> list(
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
	) {
		return sleepService.list(currentUserProvider.getCurrentUser(), from, to).stream()
			.map(SleepResponse::from)
			.toList();
	}

	@GetMapping("/{date}")
	SleepResponse get(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return SleepResponse.from(sleepService.get(currentUserProvider.getCurrentUser(), date));
	}

	@PutMapping("/{date}")
	SleepResponse save(
		@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
		@Valid @RequestBody SaveSleepRequest request
	) {
		return SleepResponse.from(
			sleepService.save(currentUserProvider.getCurrentUser(), request.toCommand(date))
		);
	}

	@DeleteMapping("/{date}")
	ResponseEntity<Void> delete(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		sleepService.delete(currentUserProvider.getCurrentUser(), date);
		return ResponseEntity.noContent().build();
	}
}

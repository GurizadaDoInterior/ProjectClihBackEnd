package com.gurizadadointerior.clih.habit.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import com.gurizadadointerior.clih.habit.application.HabitApplicationService;
import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.habit.api.dto.CreateHabitRequest;
import com.gurizadadointerior.clih.habit.api.dto.HabitResponse;
import com.gurizadadointerior.clih.habit.api.dto.UpdateHabitRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/habits")
public class HabitController {

	private final CurrentUserProvider currentUserProvider;
	private final HabitApplicationService habitService;

	public HabitController(CurrentUserProvider currentUserProvider, HabitApplicationService habitService) {
		this.currentUserProvider = currentUserProvider;
		this.habitService = habitService;
	}

	@GetMapping
	List<HabitResponse> list() {
		return habitService.list(currentUserProvider.getCurrentUser()).stream().map(HabitResponse::from).toList();
	}

	@PostMapping
	ResponseEntity<HabitResponse> create(@Valid @RequestBody CreateHabitRequest request) {
		var habit = habitService.create(currentUserProvider.getCurrentUser(), request.toCommand());
		return ResponseEntity.created(URI.create("/api/v1/habits/" + habit.getId()))
			.body(HabitResponse.from(habit));
	}

	@PatchMapping("/{habitId}")
	HabitResponse update(@PathVariable UUID habitId, @Valid @RequestBody UpdateHabitRequest request) {
		return HabitResponse.from(habitService.update(currentUserProvider.getCurrentUser(), habitId, request.toCommand()));
	}

	@DeleteMapping("/{habitId}")
	ResponseEntity<Void> archive(@PathVariable UUID habitId) {
		habitService.archive(currentUserProvider.getCurrentUser(), habitId);
		return ResponseEntity.noContent().build();
	}
}

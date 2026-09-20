package com.gurizadadointerior.clih.area.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import com.gurizadadointerior.clih.area.application.AreaApplicationService;
import com.gurizadadointerior.clih.area.api.dto.AreaResponse;
import com.gurizadadointerior.clih.area.api.dto.CreateAreaRequest;
import com.gurizadadointerior.clih.area.api.dto.UpdateAreaRequest;
import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
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
@RequestMapping("/api/v1/areas")
public class AreaController {

	private final CurrentUserProvider currentUserProvider;
	private final AreaApplicationService areaService;

	public AreaController(CurrentUserProvider currentUserProvider, AreaApplicationService areaService) {
		this.currentUserProvider = currentUserProvider;
		this.areaService = areaService;
	}

	@GetMapping
	List<AreaResponse> list() {
		return areaService.list(currentUserProvider.getCurrentUser()).stream().map(AreaResponse::from).toList();
	}

	@PostMapping
	ResponseEntity<AreaResponse> create(@Valid @RequestBody CreateAreaRequest request) {
		var area = areaService.create(currentUserProvider.getCurrentUser(), request.toCommand());
		return ResponseEntity.created(URI.create("/api/v1/areas/" + area.getId()))
			.body(AreaResponse.from(area));
	}

	@PatchMapping("/{areaId}")
	AreaResponse update(@PathVariable UUID areaId, @Valid @RequestBody UpdateAreaRequest request) {
		return AreaResponse.from(areaService.update(currentUserProvider.getCurrentUser(), areaId, request.toCommand()));
	}

	@DeleteMapping("/{areaId}")
	ResponseEntity<Void> archive(@PathVariable UUID areaId) {
		areaService.archive(currentUserProvider.getCurrentUser(), areaId);
		return ResponseEntity.noContent().build();
	}
}

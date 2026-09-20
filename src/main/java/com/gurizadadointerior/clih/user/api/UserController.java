package com.gurizadadointerior.clih.user.api;

import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.user.application.UserApplicationService;
import com.gurizadadointerior.clih.user.api.dto.UpdateMeRequest;
import com.gurizadadointerior.clih.user.api.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class UserController {

	private final CurrentUserProvider currentUserProvider;
	private final UserApplicationService userService;

	public UserController(CurrentUserProvider currentUserProvider, UserApplicationService userService) {
		this.currentUserProvider = currentUserProvider;
		this.userService = userService;
	}

	@GetMapping
	UserResponse getMe() {
		return UserResponse.from(currentUserProvider.getCurrentUser());
	}

	@PatchMapping
	UserResponse updateMe(@Valid @RequestBody UpdateMeRequest request) {
		var user = currentUserProvider.getCurrentUser();
		return UserResponse.from(userService.update(user, request.toCommand()));
	}
}

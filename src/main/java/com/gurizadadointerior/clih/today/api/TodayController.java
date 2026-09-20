package com.gurizadadointerior.clih.today.api;

import java.time.Clock;
import java.time.LocalDate;

import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import com.gurizadadointerior.clih.today.application.TodayQueryService;
import com.gurizadadointerior.clih.today.application.view.TodayResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/today")
public class TodayController {

	private final CurrentUserProvider currentUserProvider;
	private final TodayQueryService todayService;
	private final Clock clock;

	public TodayController(CurrentUserProvider currentUserProvider, TodayQueryService todayService, Clock clock) {
		this.currentUserProvider = currentUserProvider;
		this.todayService = todayService;
		this.clock = clock;
	}

	@GetMapping
	TodayResponse getToday(
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		var user = currentUserProvider.getCurrentUser();
		var requestedDate = date == null ? LocalDate.now(clock.withZone(user.zoneId())) : date;
		return todayService.getDay(user, requestedDate);
	}
}

package com.gurizadadointerior.clih.today.application.view;

public record AreaProgressView(
	TodayAreaResponse area,
	int planned,
	int completed,
	int progressPercentage
) {
}

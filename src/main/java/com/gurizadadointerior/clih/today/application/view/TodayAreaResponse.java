package com.gurizadadointerior.clih.today.application.view;

import java.util.UUID;

import com.gurizadadointerior.clih.area.domain.Area;

public record TodayAreaResponse(
	UUID id,
	String name,
	String slug,
	String color,
	String icon,
	int position
) {

	public static TodayAreaResponse from(Area area) {
		return new TodayAreaResponse(
			area.getId(), area.getName(), area.getSlug(), area.getColor(), area.getIcon(), area.getPosition()
		);
	}
}

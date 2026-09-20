package com.gurizadadointerior.clih.area.api.dto;

import java.time.Instant;
import java.util.UUID;

import com.gurizadadointerior.clih.area.domain.Area;

public record AreaResponse(
	UUID id,
	String name,
	String slug,
	String color,
	String icon,
	int position,
	Instant createdAt
) {
	public static AreaResponse from(Area area) {
		return new AreaResponse(
			area.getId(),
			area.getName(),
			area.getSlug(),
			area.getColor(),
			area.getIcon(),
			area.getPosition(),
			area.getCreatedAt()
		);
	}
}

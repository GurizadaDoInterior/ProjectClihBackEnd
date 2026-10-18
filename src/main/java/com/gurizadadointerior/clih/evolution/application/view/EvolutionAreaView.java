package com.gurizadadointerior.clih.evolution.application.view;

import java.util.UUID;

public record EvolutionAreaView(
	UUID areaId,
	String name,
	String color,
	String icon,
	int plannedActivities,
	int completedActivities,
	Integer completionPercentage
) {
}

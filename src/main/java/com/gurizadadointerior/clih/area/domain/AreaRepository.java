package com.gurizadadointerior.clih.area.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AreaRepository {

	List<Area> findAllByUserIdAndArchivedAtIsNullOrderByPositionAscCreatedAtAsc(UUID userId);

	Optional<Area> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId);

	boolean existsByUserIdAndNameIgnoreCaseAndArchivedAtIsNull(UUID userId, String name);

	boolean existsByUserIdAndNameIgnoreCaseAndIdNotAndArchivedAtIsNull(UUID userId, String name, UUID id);

	boolean existsByUserIdAndSlug(UUID userId, String slug);

	int findMaxPositionByUserId(UUID userId);

	boolean hasActiveHabits(UUID areaId);

	Area save(Area area);
}

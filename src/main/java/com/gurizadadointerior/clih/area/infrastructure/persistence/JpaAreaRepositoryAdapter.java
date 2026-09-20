package com.gurizadadointerior.clih.area.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.gurizadadointerior.clih.area.domain.Area;
import com.gurizadadointerior.clih.area.domain.AreaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
class JpaAreaRepositoryAdapter implements AreaRepository {

	private final SpringDataAreaRepository repository;

	JpaAreaRepositoryAdapter(SpringDataAreaRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Area> findAllByUserIdAndArchivedAtIsNullOrderByPositionAscCreatedAtAsc(UUID userId) {
		return repository.findAllByUserIdAndArchivedAtIsNullOrderByPositionAscCreatedAtAsc(userId);
	}

	@Override
	public Optional<Area> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId) {
		return repository.findByIdAndUserIdAndArchivedAtIsNull(id, userId);
	}

	@Override
	public boolean existsByUserIdAndNameIgnoreCaseAndArchivedAtIsNull(UUID userId, String name) {
		return repository.existsByUserIdAndNameIgnoreCaseAndArchivedAtIsNull(userId, name);
	}

	@Override
	public boolean existsByUserIdAndNameIgnoreCaseAndIdNotAndArchivedAtIsNull(UUID userId, String name, UUID id) {
		return repository.existsByUserIdAndNameIgnoreCaseAndIdNotAndArchivedAtIsNull(userId, name, id);
	}

	@Override
	public boolean existsByUserIdAndSlug(UUID userId, String slug) {
		return repository.existsByUserIdAndSlug(userId, slug);
	}

	@Override
	public int findMaxPositionByUserId(UUID userId) {
		return repository.findMaxPositionByUserId(userId);
	}

	@Override
	public boolean hasActiveHabits(UUID areaId) {
		return repository.hasActiveHabits(areaId);
	}

	@Override
	public Area save(Area area) {
		return repository.save(area);
	}
}

interface SpringDataAreaRepository extends JpaRepository<Area, UUID> {
	List<Area> findAllByUserIdAndArchivedAtIsNullOrderByPositionAscCreatedAtAsc(UUID userId);
	Optional<Area> findByIdAndUserIdAndArchivedAtIsNull(UUID id, UUID userId);
	boolean existsByUserIdAndNameIgnoreCaseAndArchivedAtIsNull(UUID userId, String name);
	boolean existsByUserIdAndNameIgnoreCaseAndIdNotAndArchivedAtIsNull(UUID userId, String name, UUID id);
	boolean existsByUserIdAndSlug(UUID userId, String slug);

	@Query("select coalesce(max(area.position), 0) from Area area where area.userId = :userId")
	int findMaxPositionByUserId(@Param("userId") UUID userId);

	@Query(value = "SELECT EXISTS (SELECT 1 FROM habits WHERE area_id = :areaId AND active = TRUE AND archived_at IS NULL)", nativeQuery = true)
	boolean hasActiveHabits(@Param("areaId") UUID areaId);
}

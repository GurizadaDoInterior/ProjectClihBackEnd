package com.gurizadadointerior.clih.area.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.gurizadadointerior.clih.area.domain.Area;
import com.gurizadadointerior.clih.area.domain.AreaRepository;
import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import com.gurizadadointerior.clih.user.domain.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AreaApplicationService {

	private final AreaRepository repository;
	private final SlugGenerator slugGenerator;
	private final Clock clock;

	public AreaApplicationService(AreaRepository repository, SlugGenerator slugGenerator, Clock clock) {
		this.repository = repository;
		this.slugGenerator = slugGenerator;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public List<Area> list(AppUser user) {
		return repository.findAllByUserIdAndArchivedAtIsNullOrderByPositionAscCreatedAtAsc(user.getId());
	}

	@Transactional
	public Area create(AppUser user, CreateAreaCommand command) {
		var name = command.name().trim();
		if (repository.existsByUserIdAndNameIgnoreCaseAndArchivedAtIsNull(user.getId(), name)) {
			throw new ApiException(ErrorCode.AREA_NAME_ALREADY_EXISTS);
		}
		var slug = slugGenerator.uniqueFor(user.getId(), name);
		var position = repository.findMaxPositionByUserId(user.getId()) + 1;
		return repository.save(new Area(
			user.getId(), name, slug, command.color().toUpperCase(), command.icon().trim(), position
		));
	}

	@Transactional
	public Area update(AppUser user, UUID areaId, UpdateAreaCommand command) {
		var area = getOwnedActive(user.getId(), areaId);
		var name = command.name() == null ? null : command.name().trim();
		if (name != null && repository.existsByUserIdAndNameIgnoreCaseAndIdNotAndArchivedAtIsNull(user.getId(), name, areaId)) {
			throw new ApiException(ErrorCode.AREA_NAME_ALREADY_EXISTS);
		}
		area.update(
			name,
			command.color() == null ? null : command.color().toUpperCase(),
			command.icon() == null ? null : command.icon().trim(),
			command.position()
		);
		return repository.save(area);
	}

	@Transactional
	public void archive(AppUser user, UUID areaId) {
		var area = getOwnedActive(user.getId(), areaId);
		if (repository.hasActiveHabits(areaId)) {
			throw new ApiException(ErrorCode.AREA_HAS_ACTIVE_HABITS);
		}
		area.archive(Instant.now(clock));
	}

	@Transactional(readOnly = true)
	public Area getOwnedActive(UUID userId, UUID areaId) {
		return repository.findByIdAndUserIdAndArchivedAtIsNull(areaId, userId)
			.orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Área não encontrada."));
	}
}

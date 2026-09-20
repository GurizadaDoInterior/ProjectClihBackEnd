package com.gurizadadointerior.clih.reward.domain;

import java.util.UUID;

import java.util.Optional;

public interface UserProgressRepository {
	Optional<UserProgress> findById(UUID userId);

	Optional<UserProgress> findByIdForUpdate(UUID userId);

	void createIfAbsent(UUID userId);

	UserProgress save(UserProgress progress);
}

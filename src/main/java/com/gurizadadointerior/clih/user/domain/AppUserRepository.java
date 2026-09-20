package com.gurizadadointerior.clih.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository {

	Optional<AppUser> findByAuthSubject(String authSubject);

	void createIfAbsent(String authSubject);

	boolean existsByUsernameIgnoreCaseAndIdNot(String username, UUID id);

	AppUser save(AppUser user);
}

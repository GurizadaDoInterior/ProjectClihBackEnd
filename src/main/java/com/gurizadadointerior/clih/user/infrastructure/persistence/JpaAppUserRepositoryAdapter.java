package com.gurizadadointerior.clih.user.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import com.gurizadadointerior.clih.user.domain.AppUser;
import com.gurizadadointerior.clih.user.domain.AppUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
class JpaAppUserRepositoryAdapter implements AppUserRepository {

	private final SpringDataAppUserRepository repository;

	JpaAppUserRepositoryAdapter(SpringDataAppUserRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<AppUser> findByAuthSubject(String authSubject) {
		return repository.findByAuthSubject(authSubject);
	}

	@Override
	public void createIfAbsent(String authSubject) {
		repository.createIfAbsent(authSubject);
	}

	@Override
	public boolean existsByUsernameIgnoreCaseAndIdNot(String username, UUID id) {
		return repository.existsByUsernameIgnoreCaseAndIdNot(username, id);
	}

	@Override
	public AppUser save(AppUser user) {
		return repository.save(user);
	}
}

interface SpringDataAppUserRepository extends JpaRepository<AppUser, UUID> {
	Optional<AppUser> findByAuthSubject(String authSubject);

	boolean existsByUsernameIgnoreCaseAndIdNot(String username, UUID id);

	@Modifying
	@Query(value = """
		INSERT INTO app_users (
		  id, auth_subject, display_name, timezone_id, profile_visibility,
		  onboarding_completed, created_at, updated_at
		) VALUES (
		  gen_random_uuid(), :subject, 'Novo usuário', 'UTC', 'PRIVATE',
		  FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
		)
		ON CONFLICT (auth_subject) DO NOTHING
		""", nativeQuery = true)
	int createIfAbsent(@Param("subject") String authSubject);
}

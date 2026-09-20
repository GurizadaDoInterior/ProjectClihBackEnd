package com.gurizadadointerior.clih.reward.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import com.gurizadadointerior.clih.reward.domain.UserProgress;
import com.gurizadadointerior.clih.reward.domain.UserProgressRepository;
import com.gurizadadointerior.clih.reward.domain.XpTransaction;
import com.gurizadadointerior.clih.reward.domain.XpTransactionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
class JpaUserProgressRepositoryAdapter implements UserProgressRepository {
	private final SpringDataUserProgressRepository repository;

	JpaUserProgressRepositoryAdapter(SpringDataUserProgressRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<UserProgress> findById(UUID userId) { return repository.findById(userId); }

	@Override
	public Optional<UserProgress> findByIdForUpdate(UUID userId) { return repository.findByIdForUpdate(userId); }

	@Override
	public void createIfAbsent(UUID userId) { repository.createIfAbsent(userId); }

	@Override
	public UserProgress save(UserProgress progress) { return repository.save(progress); }
}

@Repository
class JpaXpTransactionRepositoryAdapter implements XpTransactionRepository {
	private final SpringDataXpTransactionRepository repository;

	JpaXpTransactionRepositoryAdapter(SpringDataXpTransactionRepository repository) {
		this.repository = repository;
	}

	@Override
	public boolean existsBySourceId(UUID sourceId) { return repository.existsBySourceId(sourceId); }

	@Override
	public XpTransaction save(XpTransaction transaction) { return repository.save(transaction); }
}

interface SpringDataUserProgressRepository extends JpaRepository<UserProgress, UUID> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select progress from UserProgress progress where progress.userId = :userId")
	Optional<UserProgress> findByIdForUpdate(@Param("userId") UUID userId);

	@Modifying
	@Query(value = """
		INSERT INTO user_progress (
		  user_id, total_xp, current_level, current_streak, longest_streak, version
		) VALUES (:userId, 0, 1, 0, 0, 0)
		ON CONFLICT (user_id) DO NOTHING
		""", nativeQuery = true)
	int createIfAbsent(@Param("userId") UUID userId);
}

interface SpringDataXpTransactionRepository extends JpaRepository<XpTransaction, UUID> {
	boolean existsBySourceId(UUID sourceId);
}

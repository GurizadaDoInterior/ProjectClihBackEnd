package com.gurizadadointerior.clih;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@ActiveProfiles("local")
class ClihApiApplicationTests {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
		DockerImageName.parse("postgres:17-alpine")
	);

	@Autowired
	private EntityManager entityManager;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void databaseRejectsCrossUserHabitAreaRelationship() {
		var firstUser = UUID.randomUUID();
		var secondUser = UUID.randomUUID();
		var areaId = UUID.randomUUID();

		insertUser(firstUser, "test:" + firstUser);
		insertUser(secondUser, "test:" + secondUser);
		entityManager.createNativeQuery("""
			INSERT INTO areas (
			  id, user_id, name, slug, color, icon, position, created_at, updated_at, version
			) VALUES (:id, :userId, 'Saúde', 'saude', '#112233', 'heart', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
			""")
			.setParameter("id", areaId)
			.setParameter("userId", firstUser)
			.executeUpdate();

		assertThatThrownBy(() -> entityManager.createNativeQuery("""
			INSERT INTO habits (
			  id, user_id, area_id, name, position, active, created_at, updated_at,
			  measurement_type, target_value, day_period, start_date, version
			) VALUES (
			  :id, :userId, :areaId, 'Cross tenant', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
			  'COUNT', 1, 'ANYTIME', CURRENT_DATE, 0
			)
			""")
			.setParameter("id", UUID.randomUUID())
			.setParameter("userId", secondUser)
			.setParameter("areaId", areaId)
			.executeUpdate())
			.isInstanceOf(PersistenceException.class);
	}

	private void insertUser(UUID id, String subject) {
		entityManager.createNativeQuery("""
			INSERT INTO app_users (
			  id, auth_subject, display_name, timezone_id, profile_visibility,
			  onboarding_completed, created_at, updated_at, version
			) VALUES (
			  :id, :subject, 'Teste', 'UTC', 'PRIVATE', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
			)
			""")
			.setParameter("id", id)
			.setParameter("subject", subject)
			.executeUpdate();
	}
}

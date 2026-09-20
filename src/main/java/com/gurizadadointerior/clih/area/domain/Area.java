package com.gurizadadointerior.clih.area.domain;

import java.time.Instant;
import java.util.UUID;

import com.gurizadadointerior.clih.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "areas")
public class Area extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(nullable = false, length = 60)
	private String name;

	@Column(nullable = false, length = 30)
	private String slug;

	@Column(nullable = false, length = 7)
	private String color;

	@Column(nullable = false, length = 50)
	private String icon;

	@Column(nullable = false)
	private int position;

	@Column(name = "archived_at")
	private Instant archivedAt;

	@Version
	@Column(nullable = false)
	private long version;

	protected Area() {
	}

	public Area(UUID userId, String name, String slug, String color, String icon, int position) {
		this.userId = userId;
		this.name = name;
		this.slug = slug;
		this.color = color;
		this.icon = icon;
		this.position = position;
	}

	public void update(String name, String color, String icon, Integer position) {
		if (name != null) {
			this.name = name;
		}
		if (color != null) {
			this.color = color;
		}
		if (icon != null) {
			this.icon = icon;
		}
		if (position != null) {
			this.position = position;
		}
	}

	public void archive(Instant archivedAt) {
		this.archivedAt = archivedAt;
	}

	public UUID getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getSlug() {
		return slug;
	}

	public String getColor() {
		return color;
	}

	public String getIcon() {
		return icon;
	}

	public int getPosition() {
		return position;
	}

	public Instant getArchivedAt() {
		return archivedAt;
	}
}

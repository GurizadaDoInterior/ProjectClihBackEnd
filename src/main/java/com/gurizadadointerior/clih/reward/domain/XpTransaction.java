package com.gurizadadointerior.clih.reward.domain;

import java.util.UUID;

import com.gurizadadointerior.clih.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "point_transactions")
public class XpTransaction extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private int amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private XpReason reason;

	@Column(name = "completion_id", nullable = false, unique = true)
	private UUID sourceId;

	@Column(name = "balance_after", nullable = false)
	private long totalXpAfter;

	@Column(length = 200)
	private String description;

	protected XpTransaction() {
	}

	public XpTransaction(
		UUID userId,
		int amount,
		XpReason reason,
		UUID sourceId,
		long totalXpAfter,
		String description
	) {
		this.userId = userId;
		this.amount = amount;
		this.reason = reason;
		this.sourceId = sourceId;
		this.totalXpAfter = totalXpAfter;
		this.description = description;
	}
}

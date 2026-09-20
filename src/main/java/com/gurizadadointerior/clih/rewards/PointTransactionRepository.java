package com.gurizadadointerior.clih.rewards;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, UUID> {
}

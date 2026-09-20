package com.gurizadadointerior.clih.reward.domain;

import java.util.UUID;

public interface XpTransactionRepository {

	boolean existsBySourceId(UUID sourceId);

	XpTransaction save(XpTransaction transaction);
}

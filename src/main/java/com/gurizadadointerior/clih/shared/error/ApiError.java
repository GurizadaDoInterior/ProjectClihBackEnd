package com.gurizadadointerior.clih.shared.error;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ApiError(
	Instant timestamp,
	int status,
	String code,
	String message,
	String path,
	String traceId,
	List<FieldViolation> fieldErrors,
	Map<String, Object> details
) {
}

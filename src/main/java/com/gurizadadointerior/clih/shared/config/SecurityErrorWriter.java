package com.gurizadadointerior.clih.shared.config;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gurizadadointerior.clih.shared.error.ApiError;
import com.gurizadadointerior.clih.shared.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
class SecurityErrorWriter {

	private final ObjectMapper objectMapper;

	SecurityErrorWriter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	void write(HttpServletRequest request, HttpServletResponse response, ErrorCode code) throws java.io.IOException {
		response.setStatus(code.status().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");

		var error = new ApiError(
			Instant.now(), code.status().value(), code.name(), code.defaultMessage(),
			request.getRequestURI(), UUID.randomUUID().toString(), List.of(), Map.of()
		);
		objectMapper.writeValue(response.getOutputStream(), error);
	}
}

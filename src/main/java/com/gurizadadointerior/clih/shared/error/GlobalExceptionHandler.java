package com.gurizadadointerior.clih.shared.error;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ApiException.class)
	ResponseEntity<ApiError> handleApiException(ApiException exception, HttpServletRequest request) {
		return response(exception.getErrorCode(), exception.getMessage(), request, List.of(), exception.getDetails());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		var errors = exception.getBindingResult().getFieldErrors().stream()
			.map(error -> new FieldViolation(error.getField(), error.getDefaultMessage()))
			.toList();
		return response(ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.defaultMessage(), request, errors, Map.of());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
		var errors = exception.getConstraintViolations().stream()
			.map(error -> new FieldViolation(error.getPropertyPath().toString(), error.getMessage()))
			.toList();
		return response(ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.defaultMessage(), request, errors, Map.of());
	}

	@ExceptionHandler({
		HttpMessageNotReadableException.class,
		MethodArgumentTypeMismatchException.class,
		MissingRequestHeaderException.class
	})
	ResponseEntity<ApiError> handleMalformedRequest(Exception exception, HttpServletRequest request) {
		return response(ErrorCode.MALFORMED_REQUEST, ErrorCode.MALFORMED_REQUEST.defaultMessage(), request, List.of(), Map.of());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	ResponseEntity<ApiError> handleDataConflict(DataIntegrityViolationException exception, HttpServletRequest request) {
		log.warn("Database constraint violation at {}", request.getRequestURI());
		return response(ErrorCode.DATA_CONFLICT, ErrorCode.DATA_CONFLICT.defaultMessage(), request, List.of(), Map.of());
	}

	@ExceptionHandler(OptimisticLockingFailureException.class)
	ResponseEntity<ApiError> handleConcurrentUpdate(OptimisticLockingFailureException exception, HttpServletRequest request) {
		log.warn("Concurrent update conflict at {}", request.getRequestURI());
		return response(ErrorCode.DATA_CONFLICT, ErrorCode.DATA_CONFLICT.defaultMessage(), request, List.of(), Map.of());
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
		var traceId = UUID.randomUUID().toString();
		log.error("Unexpected error. traceId={}", traceId, exception);
		return response(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.defaultMessage(), request, List.of(), Map.of(), traceId);
	}

	private ResponseEntity<ApiError> response(
		ErrorCode code,
		String message,
		HttpServletRequest request,
		List<FieldViolation> fieldErrors,
		Map<String, Object> details
	) {
		return response(code, message, request, fieldErrors, details, UUID.randomUUID().toString());
	}

	private ResponseEntity<ApiError> response(
		ErrorCode code,
		String message,
		HttpServletRequest request,
		List<FieldViolation> fieldErrors,
		Map<String, Object> details,
		String traceId
	) {
		var body = new ApiError(
			Instant.now(),
			code.status().value(),
			code.name(),
			message,
			request.getRequestURI(),
			traceId,
			fieldErrors,
			details
		);
		return ResponseEntity.status(code.status()).body(body);
	}
}

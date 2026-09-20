package com.gurizadadointerior.clih.shared.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "Autenticação necessária."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "Você não tem permissão para esta operação."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Recurso não encontrado."),
	USERNAME_ALREADY_TAKEN(HttpStatus.CONFLICT, "Este nome de usuário já está em uso."),
	AREA_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Já existe uma área com este nome."),
	AREA_HAS_ACTIVE_HABITS(HttpStatus.CONFLICT, "Arquive ou mova os hábitos ativos antes de arquivar esta área."),
	HABIT_ALREADY_COMPLETED(HttpStatus.CONFLICT, "Esta atividade já foi concluída nesta data."),
	IDEMPOTENCY_KEY_REUSED(HttpStatus.CONFLICT, "A chave de idempotência já foi usada em outra operação."),
	INVALID_TIMEZONE(HttpStatus.BAD_REQUEST, "Fuso horário inválido."),
	INVALID_AVATAR_URL(HttpStatus.BAD_REQUEST, "A imagem do perfil deve usar uma URL HTTPS válida."),
	INVALID_AUTHENTICATION_SUBJECT(HttpStatus.UNAUTHORIZED, "Identidade de autenticação inválida."),
	INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "Intervalo de datas inválido."),
	INVALID_COMPLETION_TIME(HttpStatus.BAD_REQUEST, "Horário de conclusão inválido."),
	TARGET_NOT_REACHED(HttpStatus.UNPROCESSABLE_CONTENT, "O valor realizado ainda não atingiu a meta do hábito."),
	HABIT_NOT_SCHEDULED(HttpStatus.UNPROCESSABLE_CONTENT, "O hábito não está programado para esta data."),
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Existem campos inválidos."),
	MALFORMED_REQUEST(HttpStatus.BAD_REQUEST, "A requisição não pôde ser interpretada."),
	DATA_CONFLICT(HttpStatus.CONFLICT, "A operação conflita com o estado atual dos dados."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado.");

	private final HttpStatus status;
	private final String defaultMessage;

	ErrorCode(HttpStatus status, String defaultMessage) {
		this.status = status;
		this.defaultMessage = defaultMessage;
	}

	public HttpStatus status() {
		return status;
	}

	public String defaultMessage() {
		return defaultMessage;
	}
}

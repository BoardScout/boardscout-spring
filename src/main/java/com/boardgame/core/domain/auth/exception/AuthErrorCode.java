package com.boardgame.core.domain.auth.exception;

import com.boardgame.core.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements ErrorCode {

	EMAIL_ALREADY_EXISTS(
		HttpStatus.CONFLICT,
		"USER-409-001",
		"이미 가입된 이메일입니다."
	),
	INVALID_CREDENTIALS(
		HttpStatus.UNAUTHORIZED,
		"USER-401-001",
		"이메일 또는 비밀번호가 올바르지 않습니다."
	);

	private final HttpStatus status;
	private final String code;
	private final String message;

	AuthErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	@Override
	public HttpStatus status() {
		return status;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String message() {
		return message;
	}
}

package com.boardgame.core.domain.user.exception;

import com.boardgame.core.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

	EMAIL_ALREADY_EXISTS(
		HttpStatus.CONFLICT,
		"USER-409-001",
		"이미 가입된 이메일입니다."
	);

	private final HttpStatus status;
	private final String code;
	private final String message;

	UserErrorCode(HttpStatus status, String code, String message) {
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

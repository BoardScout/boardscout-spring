package com.boardgame.core.domain.user.exception;

import com.boardgame.core.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/** 사용자 CRUD에서 클라이언트가 구분해야 하는 오류입니다. */
public enum UserErrorCode implements ErrorCode {

	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-404-001", "사용자를 찾을 수 없습니다."),
	USER_PREFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-404-002", "저장된 사용자 취향을 찾을 수 없습니다.");

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

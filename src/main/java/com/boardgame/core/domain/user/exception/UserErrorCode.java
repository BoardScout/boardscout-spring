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

	// 에러별 HTTP 상태와 클라이언트에 전달할 내용을 묶어 둡니다.
	UserErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	// 응답에 사용할 HTTP 상태를 반환합니다.
	@Override
	public HttpStatus status() {
		return status;
	}

	// 클라이언트가 오류 종류를 구분할 때 사용할 코드를 반환합니다.
	@Override
	public String code() {
		return code;
	}

	// 화면에 표시할 기본 오류 메시지를 반환합니다.
	@Override
	public String message() {
		return message;
	}
}

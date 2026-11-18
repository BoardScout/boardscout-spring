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

	// 에러별 HTTP 상태와 클라이언트에 전달할 내용을 묶어 둡니다.
	AuthErrorCode(HttpStatus status, String code, String message) {
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

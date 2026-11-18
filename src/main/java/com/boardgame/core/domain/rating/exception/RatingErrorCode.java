package com.boardgame.core.domain.rating.exception;

import org.springframework.http.HttpStatus;

import com.boardgame.core.global.exception.ErrorCode;

public enum RatingErrorCode implements ErrorCode {

	DATASET_GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "RATING-404-001", "평가할 데이터셋 게임을 찾을 수 없습니다."),
	RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "RATING-404-002", "저장된 별점을 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	// 에러별 HTTP 상태와 클라이언트에 전달할 내용을 묶어 둡니다.
	RatingErrorCode(HttpStatus status, String code, String message) {
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

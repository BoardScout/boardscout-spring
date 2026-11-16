package com.boardgame.core.domain.rating.exception;

import org.springframework.http.HttpStatus;

import com.boardgame.core.global.exception.ErrorCode;

public enum RatingErrorCode implements ErrorCode {

	DATASET_GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "RATING-404-001", "평가할 데이터셋 게임을 찾을 수 없습니다."),
	RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "RATING-404-002", "저장된 별점을 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	RatingErrorCode(HttpStatus status, String code, String message) {
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

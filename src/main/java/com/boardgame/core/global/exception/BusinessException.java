package com.boardgame.core.global.exception;

/**
 * 예상 가능한 비즈니스 실패를 HTTP 오류 코드와 연결합니다.
 */
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	// 오류 코드를 예외 메시지와 함께 보관합니다.
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.message());
		this.errorCode = errorCode;
	}

	// 전역 예외 처리기가 상태 코드와 응답 내용을 만들 때 사용합니다.
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}

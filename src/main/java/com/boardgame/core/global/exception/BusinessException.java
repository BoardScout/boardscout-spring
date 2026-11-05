package com.boardgame.core.global.exception;

/**
 * 예상 가능한 비즈니스 실패를 HTTP 오류 코드와 연결합니다.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

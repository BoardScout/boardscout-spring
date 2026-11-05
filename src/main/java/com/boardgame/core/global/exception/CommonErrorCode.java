package com.boardgame.core.global.exception;

import org.springframework.http.HttpStatus;

/**
 * 특정 도메인에 속하지 않는 HTTP 오류입니다.
 */
public enum CommonErrorCode implements ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-401", "로그인이 필요합니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-404", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON-500",
            "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    // 에러별 HTTP 상태와 클라이언트에 전달할 내용을 묶어 둡니다.
    CommonErrorCode(HttpStatus status, String code, String message) {
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

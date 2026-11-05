package com.boardgame.core.global.exception;

import org.springframework.http.HttpStatus;

/**
 * 공통 예외 처리에 필요한 값입니다.
 */
public interface ErrorCode {

    HttpStatus status();

    String code();

    String message();
}

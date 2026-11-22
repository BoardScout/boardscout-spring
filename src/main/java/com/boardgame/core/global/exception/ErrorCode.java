package com.boardgame.core.global.exception;

import org.springframework.http.HttpStatus;

/**
 * 공통 예외 처리에 필요한 값입니다.
 */
public interface ErrorCode {

	// 오류에 맞는 HTTP 상태를 제공합니다.
	HttpStatus status();

	// 응답 본문에 넣을 고유 코드를 제공합니다.
	String code();

	// 사용자에게 전달할 기본 메시지를 제공합니다.
	String message();
}

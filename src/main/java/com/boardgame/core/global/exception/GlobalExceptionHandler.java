package com.boardgame.core.global.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.boardgame.core.global.response.ApiResponse;

/**
 * REST API 예외를 공통 오류 응답으로 변환합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// 서비스에서 발생시킨 비즈니스 예외를 지정된 HTTP 응답으로 바꿉니다.
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Object> handleBusinessException(
			BusinessException exception,
			WebRequest request
	) {
		ErrorCode errorCode = exception.getErrorCode();

		log.warn("Business exception: code={}, message={}", errorCode.code(), exception.getMessage());
		return buildResponse(exception, errorCode, null, HttpHeaders.EMPTY, request);
	}

	// 요청 DTO 검증에 실패한 필드를 이름과 메시지 형태로 정리합니다.
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request
	) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors().forEach(error ->
				fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));

		return buildResponse(exception, CommonErrorCode.BAD_REQUEST, fieldErrors, headers, request);
	}

	// JSON 문법이나 값의 타입이 잘못된 요청을 400 응답으로 처리합니다.
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException exception,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request
	) {
		log.warn("Unreadable request body: {}", exception.getMessage());

		return buildResponse(exception, CommonErrorCode.BAD_REQUEST, null, headers, request);
	}

	// 등록되지 않은 API 경로를 공통 404 응답으로 처리합니다.
	@Override
	protected ResponseEntity<Object> handleNoResourceFoundException(
			NoResourceFoundException exception,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request
	) {
		log.warn("Resource not found: {}", exception.getResourcePath());

		return buildResponse(exception, CommonErrorCode.NOT_FOUND, null, headers, request);
	}

	// 따로 처리하지 않은 예외는 내부 내용을 숨기고 500 응답으로 반환합니다.
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUnexpectedException(Exception exception, WebRequest request) {
		log.error("Unexpected server error", exception);

		return buildResponse(
				exception,
				CommonErrorCode.INTERNAL_SERVER_ERROR,
				null,
				HttpHeaders.EMPTY,
				request
		);
	}

	// 에러 코드와 부가 정보를 공통 응답 형식으로 조립합니다.
	private ResponseEntity<Object> buildResponse(
			Exception exception,
			ErrorCode errorCode,
			Object result,
			HttpHeaders headers,
			WebRequest request
	) {
		ApiResponse<Object> body = ApiResponse.failure(errorCode.code(), errorCode.message(), result);
		return handleExceptionInternal(exception, body, headers, errorCode.status(), request);
	}
}

package com.boardgame.core.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Spring과 FastAPI가 동일하게 사용할 HTTP 응답 형식입니다.
 * 컨트롤러는 실제 결과만 반환하고 성공 응답 래핑은 {@link GlobalResponseAdvice}가 담당합니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success", "code", "message", "result"})
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T result
) {

    private static final String SUCCESS_CODE = "COMMON-200";
    private static final String SUCCESS_MESSAGE = "요청에 성공했습니다.";

    // 컨트롤러의 반환값을 성공 응답으로 만듭니다.
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, result);
    }

    // 에러 코드와 상세 정보를 실패 응답으로 만듭니다.
    public static <T> ApiResponse<T> failure(String code, String message, T result) {
        return new ApiResponse<>(false, code, message, result);
    }
}

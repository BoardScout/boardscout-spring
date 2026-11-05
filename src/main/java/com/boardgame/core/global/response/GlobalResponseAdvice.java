package com.boardgame.core.global.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * REST 컨트롤러의 정상 응답을 공통 형식으로 감쌉니다.
 * 이미 ApiResponse인 응답은 다시 감싸지 않습니다.
 */
@RestControllerAdvice(annotations = RestController.class)
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    // 모든 REST 컨트롤러 응답에 공통 래핑을 적용합니다.
    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    // 이미 만든 공통 응답은 유지하고 나머지 값만 성공 응답으로 감쌉니다.
    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (body instanceof ApiResponse<?>) {
            return body;
        }

        return ApiResponse.success(body);
    }
}

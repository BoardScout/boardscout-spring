package com.boardgame.core.domain.auth.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 브라우저가 상태 변경 요청 전에 사용할 CSRF 토큰을 명시적으로 발급합니다. */
@RestController
@RequestMapping("/api/v1/auth")
public class CsrfController {

	// 이후 상태 변경 요청에 사용할 CSRF 토큰을 발급해 응답과 쿠키로 내려 줍니다.
	@GetMapping("/csrf")
	public CsrfToken csrf(CsrfToken csrfToken) {
		// 토큰 값을 읽으면 Spring Security가 HttpOnly=false인 XSRF-TOKEN 쿠키도 응답에 기록합니다.
		csrfToken.getToken();
		return csrfToken;
	}
}

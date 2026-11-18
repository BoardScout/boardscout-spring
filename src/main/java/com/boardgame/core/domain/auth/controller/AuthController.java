package com.boardgame.core.domain.auth.controller;

import com.boardgame.core.domain.auth.dto.RegisterUserRequest;
import com.boardgame.core.domain.auth.dto.LoginRequest;
import com.boardgame.core.domain.auth.dto.LoginResult;
import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.auth.service.AuthService;
import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.domain.auth.session.SessionCookieFactory;
import com.boardgame.core.domain.user.dto.UserResponse;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 HTTP 요청의 입구입니다.
 * 입력 검증과 응답 쿠키 처리를 담당하고 실제 계정·세션 로직은 각 서비스에 위임합니다.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RedisSessionService redisSessionService;
	private final SessionCookieFactory sessionCookieFactory;

	// 이메일 회원가입 요청을 받아 새 계정을 만들고 가입한 사용자 정보를 201로 응답합니다.
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {
		// DTO 검증을 통과한 가입 정보를 정규화·암호화·저장하는 서비스로 전달합니다.
		return authService.register(request);
	}

	// 이메일과 비밀번호를 확인한 뒤 세션 쿠키를 내려 주고 로그인한 사용자 정보를 응답합니다.
	@PostMapping("/login")
	public UserResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
		// 자격 증명이 맞으면 서비스가 Redis 세션을 만들고 새 sessionId를 반환합니다.
		LoginResult result = authService.login(request);
		// 브라우저는 응답의 HttpOnly 쿠키를 저장했다가 이후 요청에 자동으로 전송합니다.
		response.addHeader(HttpHeaders.SET_COOKIE, sessionCookieFactory.create(result.sessionId()).toString());
		return result.user();
	}

	// 현재 로그인 세션을 종료하고 브라우저의 세션 쿠키를 만료시킵니다.
	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(
		@AuthenticationPrincipal SessionPrincipal principal,
		HttpServletResponse response
	) {
		// 유효한 로그인 세션이 있으면 Redis에서 먼저 제거해 같은 쿠키를 다시 사용할 수 없게 합니다.
		// 이미 만료된 세션으로 요청해도 로그아웃은 성공하도록 principal이 없을 때는 삭제를 생략합니다.
		if (principal != null) {
			redisSessionService.delete(principal.sessionId());
		}
		// 서버 세션 유무와 관계없이 만료 쿠키를 내려 브라우저에 남은 로그인 상태를 정리합니다.
		response.addHeader(HttpHeaders.SET_COOKIE, sessionCookieFactory.expire().toString());
	}
}

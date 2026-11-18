package com.boardgame.core.domain.user.controller;

import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.domain.auth.session.SessionCookieFactory;
import com.boardgame.core.domain.user.dto.UpdateUserRequest;
import com.boardgame.core.domain.user.dto.UserResponse;
import com.boardgame.core.domain.user.service.UserCommandService;
import com.boardgame.core.domain.user.service.UserQueryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** 로그인한 회원이 자기 계정만 조회·수정·삭제하는 HTTP 진입점입니다. */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserCommandService userCommandService;
	private final UserQueryService userQueryService;
	private final RedisSessionService redisSessionService;
	private final SessionCookieFactory sessionCookieFactory;

	// 현재 로그인한 회원의 정보를 조회합니다.
	@GetMapping("/me")
	public UserResponse getMe(@AuthenticationPrincipal SessionPrincipal principal) {
		// 1. 인증 필터가 Redis 세션에서 복원한 사용자 ID를 서비스로 전달합니다.
		return userQueryService.getById(principal.userId());
	}

	// 현재 로그인한 회원의 닉네임을 수정합니다.
	@PatchMapping("/me")
	public UserResponse updateMe(
		@AuthenticationPrincipal SessionPrincipal principal,
		@Valid @RequestBody UpdateUserRequest request
	) {
		// 1. 요청 DTO 검증 후 현재 로그인한 회원의 닉네임만 수정합니다.
		return userCommandService.update(principal.userId(), request);
	}

	// 현재 로그인한 회원을 탈퇴 처리하고 세션과 쿠키까지 정리합니다.
	@DeleteMapping("/me")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMe(
		@AuthenticationPrincipal SessionPrincipal principal,
		HttpServletResponse response
	) {
		// 1. DB에서 회원을 먼저 삭제합니다.
		userCommandService.delete(principal.userId());
		// 2. 탈퇴 직후 같은 로그인으로 접근하지 못하도록 Redis 세션을 삭제합니다.
		redisSessionService.delete(principal.sessionId());
		// 3. 브라우저에도 만료된 쿠키를 내려 로컬 로그인 흔적을 정리합니다.
		response.addHeader(HttpHeaders.SET_COOKIE, sessionCookieFactory.expire().toString());
	}
}

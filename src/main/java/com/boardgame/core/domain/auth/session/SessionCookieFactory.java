package com.boardgame.core.domain.auth.session;

import com.boardgame.core.global.config.AuthSessionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/** Redis sessionId를 브라우저가 안전하게 보관할 인증 쿠키로 변환합니다. */
@Component
@RequiredArgsConstructor
public class SessionCookieFactory {

	private final AuthSessionProperties properties;

	// 로그인에 성공했을 때 브라우저에 내려 줄 세션 쿠키를 만듭니다.
	public ResponseCookie create(String sessionId) {
		// HttpOnly로 JavaScript 접근을 막고 SameSite=Lax로 일반적인 교차 사이트 요청의 쿠키 전송을 제한합니다.
		return ResponseCookie.from(properties.cookieName(), sessionId)
			.httpOnly(true)
			.secure(properties.secureCookie())
			.sameSite("Lax")
			.path("/")
			.maxAge(properties.ttl())
			.build();
	}

	// 로그아웃이나 탈퇴 후 브라우저의 세션 쿠키를 지우기 위한 만료 쿠키를 만듭니다.
	public ResponseCookie expire() {
		// 브라우저에 같은 이름과 경로의 빈 쿠키를 내려 현재 로그인 쿠키를 제거합니다.
		return ResponseCookie.from(properties.cookieName(), "")
			.httpOnly(true)
			.secure(properties.secureCookie())
			.sameSite("Lax")
			.path("/")
			.maxAge(0)
			.build();
	}
}

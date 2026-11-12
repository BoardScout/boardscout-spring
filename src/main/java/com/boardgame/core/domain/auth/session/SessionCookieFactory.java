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
}

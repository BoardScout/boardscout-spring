package com.boardgame.core.domain.auth.session;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCookieFactory {

	private final SessionProperties properties;

	public ResponseCookie create(String sessionId) {
		return ResponseCookie.from(properties.cookieName(), sessionId)
			.httpOnly(true)
			.secure(properties.secureCookie())
			.sameSite("Lax")
			.path("/")
			.maxAge(properties.ttl())
			.build();
	}
}

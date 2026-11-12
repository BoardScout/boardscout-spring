package com.boardgame.core.domain.auth.session;

import com.boardgame.core.global.config.AuthSessionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCookieFactory {

	private final AuthSessionProperties properties;

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

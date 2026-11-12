package com.boardgame.core.domain.auth.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

class SessionCookieFactoryTest {

	@Test
	void createsHttpOnlySameSiteCookie() {
		SessionProperties properties = new SessionProperties("BGSESSION", Duration.ofMinutes(30), true);
		SessionCookieFactory factory = new SessionCookieFactory(properties);

		ResponseCookie cookie = factory.create("session-id");

		assertThat(cookie.getName()).isEqualTo("BGSESSION");
		assertThat(cookie.getValue()).isEqualTo("session-id");
		assertThat(cookie.isHttpOnly()).isTrue();
		assertThat(cookie.isSecure()).isTrue();
		assertThat(cookie.getSameSite()).isEqualTo("Lax");
		assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofMinutes(30));
		assertThat(cookie.getPath()).isEqualTo("/");
		assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofMinutes(30));
	}
}

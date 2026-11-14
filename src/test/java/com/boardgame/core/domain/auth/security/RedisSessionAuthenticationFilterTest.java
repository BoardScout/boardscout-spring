package com.boardgame.core.domain.auth.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.global.config.AuthSessionProperties;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class RedisSessionAuthenticationFilterTest {

	private static final String SESSION_ID = "a".repeat(43);

	@Mock
	private RedisSessionService redisSessionService;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void authenticatesRequestFromSharedSessionCookie() throws Exception {
		SessionPrincipal principal = new SessionPrincipal(
			SESSION_ID, 1L, "player@example.com", "플레이어", "USER");
		given(redisSessionService.findAndRefresh(SESSION_ID)).willReturn(Optional.of(principal));
		RedisSessionAuthenticationFilter filter = new RedisSessionAuthenticationFilter(
			redisSessionService,
			new AuthSessionProperties("BGSESSION", Duration.ofMinutes(30), false));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users/me");
		request.setCookies(new Cookie("BGSESSION", SESSION_ID));

		filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

		assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(principal);
		assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			.extracting("authority")
			.containsExactly("ROLE_USER");
	}
}

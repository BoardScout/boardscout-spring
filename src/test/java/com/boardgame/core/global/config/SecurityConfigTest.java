package com.boardgame.core.global.config;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boardgame.core.domain.auth.controller.CsrfController;
import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.domain.boardgame.controller.BoardGameController;
import com.boardgame.core.domain.boardgame.dto.BoardGameSearchResult;
import com.boardgame.core.domain.boardgame.dto.BoardGameCursorResult;
import com.boardgame.core.domain.boardgame.service.BoardGameQueryService;
import com.boardgame.core.domain.user.controller.UserPreferenceController;
import com.boardgame.core.domain.user.dto.UserPreferenceRequest;
import com.boardgame.core.domain.user.dto.UserPreferenceResponse;
import com.boardgame.core.domain.user.entity.PreferencePlayStyle;
import com.boardgame.core.domain.user.entity.PreferenceTheme;
import com.boardgame.core.domain.user.entity.RecommendationMode;
import com.boardgame.core.domain.user.service.UserPreferenceService;
import com.boardgame.core.health.HealthController;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({HealthController.class, BoardGameController.class, CsrfController.class, UserPreferenceController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RedisSessionService redisSessionService;

	@MockitoBean
	private AuthSessionProperties sessionProperties;

	@MockitoBean
	private BoardGameQueryService boardGameQueryService;

	@MockitoBean
	private UserPreferenceService userPreferenceService;

	@BeforeEach
	void setUp() {
		given(sessionProperties.cookieName()).willReturn("BGSESSION");
	}

	@Test
	void allowsPublicHealthEndpoint() throws Exception {
		mockMvc.perform(get("/api/v1/health"))
			.andExpect(status().isOk());
	}

	@Test
	void allowsPublicBoardGameSearchEndpoint() throws Exception {
		given(boardGameQueryService.search("팬데믹", 0, 20))
			.willReturn(new BoardGameSearchResult(List.of(), 0, 20, 0, 0));

		mockMvc.perform(get("/api/v1/board-games").param("query", "팬데믹"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.items").isArray());
	}

	@Test
	void allowsPublicCursorBoardGameEndpoint() throws Exception {
		given(boardGameQueryService.searchAfter("", null, 24))
			.willReturn(new BoardGameCursorResult(List.of(), null, false));

		mockMvc.perform(get("/api/v1/board-games/cursor"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.items").isArray())
			.andExpect(jsonPath("$.result.hasNext").value(false));
	}

	@Test
	void rejectsUnauthenticatedCoreEndpoint() throws Exception {
		mockMvc.perform(get("/api/v1/users/me"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON-401"));
	}

	@Test
	void issuesReadableCsrfCookie() throws Exception {
		mockMvc.perform(get("/api/v1/auth/csrf"))
			.andExpect(status().isOk())
			.andExpect(result -> {
				Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");
				if (cookie == null || cookie.isHttpOnly()) {
					throw new AssertionError("JavaScript가 읽을 수 있는 XSRF-TOKEN 쿠키가 필요합니다.");
				}
			})
			.andExpect(jsonPath("$.result.token").isNotEmpty());
	}

	@Test
	void requiresCookieTokenHeaderForProtectedWriteRequest() throws Exception {
		SessionPrincipal principal = new SessionPrincipal(
			"a".repeat(43), 1L, "player@example.com", "플레이어", "USER");
		var authentication = UsernamePasswordAuthenticationToken.authenticated(principal, null, List.of());
		String requestBody = """
			{"recommendationMode":"context","playerCount":4,"maxPlayTime":60,"difficulty":2,
			 "themes":["strategy"],"playStyles":["coop"],"likedGameIds":[]}
			""";
		given(userPreferenceService.save(org.mockito.ArgumentMatchers.eq(1L),
			org.mockito.ArgumentMatchers.any(UserPreferenceRequest.class)))
			.willReturn(new UserPreferenceResponse(
				RecommendationMode.CONTEXT, 4, 60, 2,
				List.of(PreferenceTheme.STRATEGY), List.of(PreferencePlayStyle.COOP), List.of()));

		// 로그인 정보만 있고 CSRF 토큰이 없으면 상태 변경 요청은 거부되어야 합니다.
		mockMvc.perform(put("/api/v1/users/me/preferences")
				.with(authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isForbidden());

		Cookie csrfCookie = mockMvc.perform(get("/api/v1/auth/csrf"))
			.andReturn().getResponse().getCookie("XSRF-TOKEN");
		if (csrfCookie == null) {
			throw new AssertionError("XSRF-TOKEN 쿠키가 발급되지 않았습니다.");
		}

		// 쿠키의 값을 X-XSRF-TOKEN 헤더로 되돌려 보내면 같은 요청이 정상 처리됩니다.
		mockMvc.perform(put("/api/v1/users/me/preferences")
				.with(authentication(authentication))
				.cookie(csrfCookie)
				.header("X-XSRF-TOKEN", csrfCookie.getValue())
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.recommendationMode").value("context"));
	}
}

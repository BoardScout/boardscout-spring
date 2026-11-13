package com.boardgame.core.global.config;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.domain.boardgame.controller.BoardGameController;
import com.boardgame.core.domain.boardgame.dto.BoardGameSearchResult;
import com.boardgame.core.domain.boardgame.dto.BoardGameCursorResult;
import com.boardgame.core.domain.boardgame.service.BoardGameCommandService;
import com.boardgame.core.domain.boardgame.service.BoardGameQueryService;
import com.boardgame.core.health.HealthController;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({HealthController.class, BoardGameController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RedisSessionService redisSessionService;

	@MockitoBean
	private AuthSessionProperties sessionProperties;

	@MockitoBean
	private BoardGameCommandService boardGameCommandService;

	@MockitoBean
	private BoardGameQueryService boardGameQueryService;

	@BeforeEach
	void setUp() {
		given(sessionProperties.cookieName()).willReturn("BGSESSION");
	}

	@Test
	void allowsPublicHealthEndpoint() throws Exception {
		mockMvc.perform(get("/api/core/health"))
			.andExpect(status().isOk());
	}

	@Test
	void allowsPublicBoardGameSearchEndpoint() throws Exception {
		given(boardGameQueryService.search("팬데믹", 0, 20))
			.willReturn(new BoardGameSearchResult(List.of(), 0, 20, 0, 0));

		mockMvc.perform(get("/api/core/board-games").param("query", "팬데믹"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.items").isArray());
	}

	@Test
	void allowsPublicCursorBoardGameEndpoint() throws Exception {
		given(boardGameQueryService.searchAfter("", null, 24))
			.willReturn(new BoardGameCursorResult(List.of(), null, false));

		mockMvc.perform(get("/api/core/board-games/cursor"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.items").isArray())
			.andExpect(jsonPath("$.result.hasNext").value(false));
	}

	@Test
	void rejectsUnauthenticatedCoreEndpoint() throws Exception {
		mockMvc.perform(get("/api/core/users/me"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON-401"));
	}
}

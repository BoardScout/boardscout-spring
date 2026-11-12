package com.boardgame.core.global.config;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.health.HealthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RedisSessionService redisSessionService;

	@MockitoBean
	private AuthSessionProperties sessionProperties;

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
	void rejectsUnauthenticatedCoreEndpoint() throws Exception {
		mockMvc.perform(get("/api/core/users/me"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON-401"));
	}
}

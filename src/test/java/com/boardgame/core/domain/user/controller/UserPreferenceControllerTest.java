package com.boardgame.core.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.user.dto.UserPreferenceRequest;
import com.boardgame.core.domain.user.dto.UserPreferenceResponse;
import com.boardgame.core.domain.user.entity.PreferencePlayStyle;
import com.boardgame.core.domain.user.entity.PreferenceTheme;
import com.boardgame.core.domain.user.entity.RecommendationMode;
import com.boardgame.core.domain.user.service.UserPreferenceService;
import com.boardgame.core.global.exception.GlobalExceptionHandler;
import com.boardgame.core.global.response.GlobalResponseAdvice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserPreferenceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class UserPreferenceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserPreferenceService userPreferenceService;

	@BeforeEach
	void setUp() {
		SessionPrincipal principal = new SessionPrincipal(
			"a".repeat(43), 1L, "player@example.com", "플레이어", "USER");
		SecurityContextHolder.getContext().setAuthentication(
			UsernamePasswordAuthenticationToken.authenticated(principal, null, List.of()));
	}

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void savesContextPreferencesForCurrentUser() throws Exception {
		given(userPreferenceService.save(any(), any(UserPreferenceRequest.class)))
			.willReturn(contextPreference());

		mockMvc.perform(put("/api/v1/users/me/preferences")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"recommendationMode":"context","playerCount":4,"maxPlayTime":60,"difficulty":2,
					 "themes":["mystery","strategy"],"playStyles":["coop","social"],"likedGameIds":[]}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.recommendationMode").value("context"))
			.andExpect(jsonPath("$.result.themes[0]").value("mystery"));

		verify(userPreferenceService).save(any(), any(UserPreferenceRequest.class));
	}

	@Test
	void rejectsTastePreferencesWithFewerThanThreeGames() throws Exception {
		mockMvc.perform(put("/api/v1/users/me/preferences")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"recommendationMode":"taste","likedGameIds":[0,1]}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	void getsCurrentUserPreferences() throws Exception {
		given(userPreferenceService.get(1L)).willReturn(contextPreference());

		mockMvc.perform(get("/api/v1/users/me/preferences"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.playerCount").value(4));
	}

	@Test
	void getsPreferencesForRecommendationService() throws Exception {
		given(userPreferenceService.get(7L)).willReturn(contextPreference());

		mockMvc.perform(get("/api/v1/internal/users/7/preferences"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.maxPlayTime").value(60));
	}

	private UserPreferenceResponse contextPreference() {
		return new UserPreferenceResponse(
			RecommendationMode.CONTEXT,
			4,
			60,
			2,
			List.of(PreferenceTheme.MYSTERY, PreferenceTheme.STRATEGY),
			List.of(PreferencePlayStyle.COOP, PreferencePlayStyle.SOCIAL),
			List.of());
	}
}

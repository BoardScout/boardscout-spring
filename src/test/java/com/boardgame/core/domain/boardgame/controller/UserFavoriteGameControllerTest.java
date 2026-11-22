package com.boardgame.core.domain.boardgame.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.boardgame.dto.UserFavoriteGameResponse;
import com.boardgame.core.domain.boardgame.service.UserFavoriteGameService;
import com.boardgame.core.global.exception.GlobalExceptionHandler;
import com.boardgame.core.global.response.GlobalResponseAdvice;

@WebMvcTest(UserFavoriteGameController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class UserFavoriteGameControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserFavoriteGameService favoriteService;

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
	void addsFavoriteByDatasetItemIdForCurrentUser() throws Exception {
		given(favoriteService.add(1L, 15)).willReturn(azul());

		mockMvc.perform(put("/api/v1/users/me/favorites/15"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.datasetItemId").value(15))
			.andExpect(jsonPath("$.result.nameEn").value("Azul"));
	}

	@Test
	void listsAndRemovesCurrentUsersFavorites() throws Exception {
		given(favoriteService.getAll(1L)).willReturn(List.of(azul()));

		mockMvc.perform(get("/api/v1/users/me/favorites"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result[0].datasetItemId").value(15))
			.andExpect(jsonPath("$.result[0].maxPlayTimeMinutes").value(45));
		mockMvc.perform(delete("/api/v1/users/me/favorites/15"))
			.andExpect(status().isOk());

		verify(favoriteService).remove(1L, 15);
	}

	private UserFavoriteGameResponse azul() {
		return new UserFavoriteGameResponse(
			15, 230802L, "Azul", "아줄", "Abstract", 2, 4, 45, new BigDecimal("1.7639"), null);
	}
}

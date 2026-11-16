package com.boardgame.core.domain.rating.controller;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.rating.dto.UserGameRatingRequest;
import com.boardgame.core.domain.rating.dto.UserGameRatingResponse;
import com.boardgame.core.domain.rating.service.UserGameRatingService;
import com.boardgame.core.global.exception.GlobalExceptionHandler;
import com.boardgame.core.global.response.GlobalResponseAdvice;

@WebMvcTest(UserGameRatingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class UserGameRatingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserGameRatingService ratingService;

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
	void savesBggScaleRatingForCurrentUser() throws Exception {
		given(ratingService.save(any(), any(), any(UserGameRatingRequest.class)))
			.willReturn(new UserGameRatingResponse(15, new BigDecimal("8.5"), null));

		mockMvc.perform(put("/api/v1/users/me/ratings/15")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"rating\":8.5}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.datasetItemId").value(15))
			.andExpect(jsonPath("$.result.rating").value(8.5));
	}

	@Test
	void rejectsRatingOutsideBggScale() throws Exception {
		mockMvc.perform(put("/api/v1/users/me/ratings/15")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"rating\":11}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void listsAndDeletesCurrentUsersRatings() throws Exception {
		given(ratingService.getAll(1L))
			.willReturn(List.of(new UserGameRatingResponse(15, new BigDecimal("9"), null)));

		mockMvc.perform(get("/api/v1/users/me/ratings"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result[0].datasetItemId").value(15));
		mockMvc.perform(delete("/api/v1/users/me/ratings/15"))
			.andExpect(status().isOk());

		verify(ratingService).delete(1L, 15);
	}
}

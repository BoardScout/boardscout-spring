package com.boardgame.core.domain.user.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.domain.auth.session.SessionCookieFactory;
import com.boardgame.core.domain.user.dto.UpdateUserRequest;
import com.boardgame.core.domain.user.dto.UserResponse;
import com.boardgame.core.domain.user.service.UserCommandService;
import com.boardgame.core.domain.user.service.UserQueryService;
import com.boardgame.core.global.exception.GlobalExceptionHandler;
import com.boardgame.core.global.response.GlobalResponseAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class UserControllerTest {

	private static final String SESSION_ID = "a".repeat(43);

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserCommandService userCommandService;

	@MockitoBean
	private UserQueryService userQueryService;

	@MockitoBean
	private RedisSessionService redisSessionService;

	@MockitoBean
	private SessionCookieFactory sessionCookieFactory;

	private UsernamePasswordAuthenticationToken authentication;

	@BeforeEach
	void setUp() {
		SessionPrincipal principal = new SessionPrincipal(
			SESSION_ID, 1L, "player@example.com", "플레이어", "USER");
		authentication = UsernamePasswordAuthenticationToken.authenticated(principal, null, java.util.List.of());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void getsCurrentUser() throws Exception {
		given(userQueryService.getById(1L))
			.willReturn(new UserResponse(1L, "player@example.com", "플레이어"));

		mockMvc.perform(get("/api/v1/users/me"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.id").value(1))
			.andExpect(jsonPath("$.result.email").value("player@example.com"));
	}

	@Test
	void updatesCurrentUserNickname() throws Exception {
		given(userCommandService.update(any(), any(UpdateUserRequest.class)))
			.willReturn(new UserResponse(1L, "player@example.com", "새 닉네임"));

		mockMvc.perform(patch("/api/v1/users/me")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nickname\":\"새 닉네임\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.nickname").value("새 닉네임"));
	}

	@Test
	void rejectsBlankNickname() throws Exception {
		mockMvc.perform(patch("/api/v1/users/me")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nickname\":\" \"}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.result.nickname").value("닉네임을 입력해 주세요."));
	}

	@Test
	void deletesCurrentUserAndClearsSession() throws Exception {
		given(sessionCookieFactory.expire()).willReturn(
			ResponseCookie.from("BGSESSION", "").path("/").maxAge(0).build());

		mockMvc.perform(delete("/api/v1/users/me"))
			.andExpect(status().isNoContent())
			.andExpect(header().string("Set-Cookie", containsString("BGSESSION=")))
			.andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));

		verify(userCommandService).delete(1L);
		verify(redisSessionService).delete(SESSION_ID);
	}
}

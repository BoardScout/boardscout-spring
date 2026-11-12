package com.boardgame.core.domain.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boardgame.core.domain.auth.dto.LoginRequest;
import com.boardgame.core.domain.auth.dto.LoginResult;
import com.boardgame.core.domain.auth.dto.RegisterUserRequest;
import com.boardgame.core.domain.auth.exception.AuthErrorCode;
import com.boardgame.core.domain.auth.service.AuthService;
import com.boardgame.core.domain.auth.session.SessionCookieFactory;
import com.boardgame.core.domain.user.dto.UserResponse;
import com.boardgame.core.global.exception.BusinessException;
import com.boardgame.core.global.exception.GlobalExceptionHandler;
import com.boardgame.core.global.response.GlobalResponseAdvice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private SessionCookieFactory sessionCookieFactory;

	@Test
	void registersUser() throws Exception {
		given(authService.register(any(RegisterUserRequest.class)))
			.willReturn(new UserResponse(1L, "player@example.com", "플레이어"));

		mockMvc.perform(post("/api/core/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "player@example.com",
					  "nickname": "플레이어",
					  "password": "password123",
					  "passwordConfirm": "password123"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.result.id").value(1))
			.andExpect(jsonPath("$.result.email").value("player@example.com"))
			.andExpect(jsonPath("$.result.nickname").value("플레이어"));
	}

	@Test
	void rejectsInvalidEmailAndMismatchedPassword() throws Exception {
		mockMvc.perform(post("/api/core/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "invalid-email",
					  "nickname": "플레이어",
					  "password": "password123",
					  "passwordConfirm": "different123"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON-400"))
			.andExpect(jsonPath("$.result.email").value("올바른 이메일 형식으로 입력해 주세요."))
			.andExpect(jsonPath("$.result.passwordConfirmed").value("비밀번호가 서로 다릅니다."));
	}

	@Test
	void rejectsDuplicateEmail() throws Exception {
		given(authService.register(any(RegisterUserRequest.class)))
			.willThrow(new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS));

		mockMvc.perform(post("/api/core/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "player@example.com",
					  "nickname": "플레이어",
					  "password": "password123",
					  "passwordConfirm": "password123"
					}
					"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("USER-409-001"))
			.andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."));
	}

	@Test
	void logsInAndSetsSharedSessionCookie() throws Exception {
		UserResponse user = new UserResponse(1L, "player@example.com", "플레이어");
		given(authService.login(any(LoginRequest.class))).willReturn(new LoginResult(user, "session-id"));
		given(sessionCookieFactory.create("session-id")).willReturn(
			ResponseCookie.from("BGSESSION", "session-id")
				.httpOnly(true)
				.sameSite("Lax")
				.path("/")
				.build());

		mockMvc.perform(post("/api/core/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "player@example.com",
					  "password": "password123"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(header().string("Set-Cookie", containsString("BGSESSION=session-id")))
			.andExpect(header().string("Set-Cookie", containsString("HttpOnly")))
			.andExpect(header().string("Set-Cookie", containsString("SameSite=Lax")))
			.andExpect(jsonPath("$.result.email").value("player@example.com"));
	}

	@Test
	void rejectsInvalidLoginRequest() throws Exception {
		mockMvc.perform(post("/api/core/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "not-an-email",
					  "password": "short"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.result.email").value("올바른 이메일 형식으로 입력해 주세요."))
			.andExpect(jsonPath("$.result.password").value("비밀번호는 8자 이상 64자 이하여야 합니다."));
	}

	@Test
	void rejectsInvalidCredentialsWithoutRevealingWhichFieldFailed() throws Exception {
		given(authService.login(any(LoginRequest.class)))
			.willThrow(new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

		mockMvc.perform(post("/api/core/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "email": "player@example.com",
					  "password": "wrongpass"
					}
					"""))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("USER-401-001"))
			.andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
	}
}

package com.boardgame.core.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.boardgame.core.domain.auth.dto.LoginRequest;
import com.boardgame.core.domain.auth.dto.LoginResult;
import com.boardgame.core.domain.auth.dto.RegisterUserRequest;
import com.boardgame.core.domain.auth.exception.AuthErrorCode;
import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.domain.user.dto.UserResponse;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RedisSessionService redisSessionService;

	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(userRepository, passwordEncoder, redisSessionService);
	}

	@Test
	void logsInWithNormalizedEmailAndCreatesSession() {
		User user = persistedUser();
		given(userRepository.findByEmailIgnoreCase("player@example.com")).willReturn(Optional.of(user));
		given(passwordEncoder.matches("password123", "encoded-password")).willReturn(true);
		given(redisSessionService.create(user)).willReturn("session-id");

		LoginResult result = authService.login(
			new LoginRequest(" Player@Example.COM ", "password123"));

		assertThat(result.user().id()).isEqualTo(1L);
		assertThat(result.user().email()).isEqualTo("player@example.com");
		assertThat(result.sessionId()).isEqualTo("session-id");
	}

	@Test
	void registersUserWithNormalizedEmailAndEncodedPassword() {
		RegisterUserRequest request = new RegisterUserRequest(
			" Player@Example.COM ", " 플레이어 ", "password123", "password123");
		given(userRepository.existsByEmailIgnoreCase("player@example.com")).willReturn(false);
		given(passwordEncoder.encode("password123")).willReturn("encoded-password");
		given(userRepository.saveAndFlush(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

		UserResponse response = authService.register(request);

		assertThat(response.email()).isEqualTo("player@example.com");
		assertThat(response.nickname()).isEqualTo("플레이어");
		verify(passwordEncoder).encode("password123");
		verify(userRepository).saveAndFlush(any(User.class));
	}

	@Test
	void rejectsDuplicateRegistrationEmail() {
		RegisterUserRequest request = new RegisterUserRequest(
			"player@example.com", "플레이어", "password123", "password123");
		given(userRepository.existsByEmailIgnoreCase("player@example.com")).willReturn(true);

		assertThatThrownBy(() -> authService.register(request))
			.isInstanceOf(BusinessException.class)
			.extracting(exception -> ((BusinessException) exception).getErrorCode())
			.isEqualTo(AuthErrorCode.EMAIL_ALREADY_EXISTS);
	}

	@Test
	void rejectsUnknownEmailAfterDummyPasswordCheck() {
		given(userRepository.findByEmailIgnoreCase("unknown@example.com")).willReturn(Optional.empty());
		given(passwordEncoder.matches(any(), any())).willReturn(false);

		assertInvalidCredentials(
			new LoginRequest("unknown@example.com", "password123"));

		verify(passwordEncoder).matches(any(), any());
	}

	@Test
	void rejectsWrongPasswordWithoutCreatingSession() {
		User user = persistedUser();
		given(userRepository.findByEmailIgnoreCase("player@example.com")).willReturn(Optional.of(user));
		given(passwordEncoder.matches("wrongpass", "encoded-password")).willReturn(false);

		assertInvalidCredentials(new LoginRequest("player@example.com", "wrongpass"));

		verify(redisSessionService, org.mockito.Mockito.never()).create(user);
	}

	private void assertInvalidCredentials(LoginRequest request) {
		assertThatThrownBy(() -> authService.login(request))
			.isInstanceOf(BusinessException.class)
			.extracting(exception -> ((BusinessException) exception).getErrorCode())
			.isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);
	}

	private User persistedUser() {
		User user = User.create("player@example.com", "encoded-password", "플레이어");
		ReflectionTestUtils.setField(user, "id", 1L);
		return user;
	}
}

package com.boardgame.core.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.boardgame.core.domain.user.dto.RegisterUserRequest;
import com.boardgame.core.domain.user.dto.UserResponse;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.exception.UserErrorCode;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	private UserCommandService userCommandService;

	@BeforeEach
	void setUp() {
		userCommandService = new UserCommandService(userRepository, passwordEncoder);
	}

	@Test
	void registersUserWithNormalizedEmailAndEncodedPassword() {
		RegisterUserRequest request = new RegisterUserRequest(
			" Player@Example.COM ", " 플레이어 ", "password123", "password123");
		given(userRepository.existsByEmailIgnoreCase("player@example.com")).willReturn(false);
		given(passwordEncoder.encode("password123")).willReturn("encoded-password");
		given(userRepository.saveAndFlush(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

		UserResponse response = userCommandService.register(request);

		assertThat(response.email()).isEqualTo("player@example.com");
		assertThat(response.nickname()).isEqualTo("플레이어");
		verify(passwordEncoder).encode("password123");
		verify(userRepository).saveAndFlush(any(User.class));
	}

	@Test
	void rejectsDuplicateEmailBeforeEncodingPassword() {
		RegisterUserRequest request = new RegisterUserRequest(
			"player@example.com", "플레이어", "password123", "password123");
		given(userRepository.existsByEmailIgnoreCase("player@example.com")).willReturn(true);

		assertThatThrownBy(() -> userCommandService.register(request))
			.isInstanceOf(BusinessException.class)
			.extracting(exception -> ((BusinessException) exception).getErrorCode())
			.isEqualTo(UserErrorCode.EMAIL_ALREADY_EXISTS);

		verify(userRepository).existsByEmailIgnoreCase("player@example.com");
	}
}

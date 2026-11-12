package com.boardgame.core.domain.auth.service;

import java.util.Locale;

import com.boardgame.core.domain.auth.dto.LoginRequest;
import com.boardgame.core.domain.auth.dto.LoginResult;
import com.boardgame.core.domain.auth.dto.RegisterUserRequest;
import com.boardgame.core.domain.auth.exception.AuthErrorCode;
import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.domain.user.dto.UserResponse;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final String DUMMY_PASSWORD_HASH =
		"$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RedisSessionService redisSessionService;

	@Transactional
	public UserResponse register(RegisterUserRequest request) {
		String email = request.email().strip().toLowerCase(Locale.ROOT);
		String nickname = request.nickname().strip();

		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
		}

		User user = User.create(email, passwordEncoder.encode(request.password()), nickname);
		try {
			return UserResponse.from(userRepository.saveAndFlush(user));
		} catch (DataIntegrityViolationException exception) {
			throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
		}
	}

	@Transactional(readOnly = true)
	public LoginResult login(LoginRequest request) {
		String email = request.email().strip().toLowerCase(Locale.ROOT);
		var user = userRepository.findByEmailIgnoreCase(email);
		String passwordHash = user.map(User::getPasswordHash).orElse(DUMMY_PASSWORD_HASH);

		if (!passwordEncoder.matches(request.password(), passwordHash) || user.isEmpty()) {
			throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
		}

		User authenticatedUser = user.orElseThrow();
		String sessionId = redisSessionService.create(authenticatedUser);
		return new LoginResult(UserResponse.from(authenticatedUser), sessionId);
	}
}

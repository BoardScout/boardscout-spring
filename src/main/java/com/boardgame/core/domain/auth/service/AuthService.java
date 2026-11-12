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

/**
 * 회원가입과 로그인 유스케이스를 실행합니다.
 * 사용자 영속성은 UserRepository에, 로그인 세션 저장은 RedisSessionService에 위임합니다.
 */
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
		// 저장과 조회가 같은 식별자를 사용하도록 이메일과 닉네임의 불필요한 공백을 정리합니다.
		String email = request.email().strip().toLowerCase(Locale.ROOT);
		String nickname = request.nickname().strip();

		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
		}

		// 비밀번호 원문은 엔티티나 DB에 전달하지 않고 BCrypt 해시로 즉시 변환합니다.
		User user = User.create(email, passwordEncoder.encode(request.password()), nickname);
		try {
			return UserResponse.from(userRepository.saveAndFlush(user));
		} catch (DataIntegrityViolationException exception) {
			// 동시에 같은 이메일로 가입한 요청도 DB 유니크 제약을 동일한 409 오류로 변환합니다.
			throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
		}
	}

	@Transactional(readOnly = true)
	public LoginResult login(LoginRequest request) {
		String email = request.email().strip().toLowerCase(Locale.ROOT);
		var user = userRepository.findByEmailIgnoreCase(email);
		// 존재하지 않는 계정도 BCrypt 비교를 실행해 이메일 존재 여부가 응답 시간으로 드러나지 않게 합니다.
		String passwordHash = user.map(User::getPasswordHash).orElse(DUMMY_PASSWORD_HASH);

		if (!passwordEncoder.matches(request.password(), passwordHash) || user.isEmpty()) {
			throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
		}

		User authenticatedUser = user.orElseThrow();
		// 인증이 끝난 뒤에만 FastAPI도 읽을 수 있는 Redis JSON 세션을 새로 발급합니다.
		String sessionId = redisSessionService.create(authenticatedUser);
		return new LoginResult(UserResponse.from(authenticatedUser), sessionId);
	}
}

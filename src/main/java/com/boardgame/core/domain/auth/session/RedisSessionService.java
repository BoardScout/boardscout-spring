package com.boardgame.core.domain.auth.session;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Optional;

import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.global.config.AuthSessionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

/**
 * Spring과 FastAPI가 함께 읽는 Redis JSON 세션의 생성·조회·TTL 갱신을 담당합니다.
 * Redis 키는 session:{sessionId}, 값은 언어에 종속되지 않는 JSON 형식을 사용합니다.
 */
@Service
@RequiredArgsConstructor
public class RedisSessionService {

	private static final String SESSION_KEY_PREFIX = "session:";
	private static final int SESSION_ID_BYTES = 32;

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private final AuthSessionProperties properties;
	private final SecureRandom secureRandom = new SecureRandom();

	// 사용자 정보를 담은 새 세션을 Redis에 저장하고 쿠키에 담을 sessionId를 반환합니다.
	public String create(User user) {
		// 로그인할 때마다 예측하기 어려운 새 ID를 만들어 기존 쿠키 값과 분리합니다.
		String sessionId = generateSessionId();
		SessionData payload = new SessionData(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			user.getRole().name(),
			OffsetDateTime.now(ZoneOffset.UTC).toString());
		String json = objectMapper.writeValueAsString(payload);

		// JSON 저장과 동시에 TTL을 설정해 로그아웃하지 않은 세션도 자동 만료되게 합니다.
		redisTemplate.opsForValue().set(SESSION_KEY_PREFIX + sessionId, json, properties.ttl());
		return sessionId;
	}

	// sessionId로 세션을 조회해 사용자 정보로 복원하고 만료 시간을 연장합니다. 세션이 없거나 손상됐으면 빈 값을 반환합니다.
	public Optional<SessionPrincipal> findAndRefresh(String sessionId) {
		// 쿠키 입력을 먼저 제한해 임의 문자열로 Redis 키를 조회하지 않도록 합니다.
		if (sessionId == null || !sessionId.matches("[A-Za-z0-9_-]{43}")) {
			return Optional.empty();
		}

		String key = SESSION_KEY_PREFIX + sessionId;
		String json = redisTemplate.opsForValue().get(key);
		if (json == null) {
			return Optional.empty();
		}

		try {
			SessionData data = objectMapper.readValue(json, SessionData.class);
			// 인증에 성공한 요청은 Redis 만료 시간을 다시 늘리는 슬라이딩 TTL을 적용합니다.
			redisTemplate.expire(key, properties.ttl());
			return Optional.of(new SessionPrincipal(
				sessionId, data.userId(), data.email(), data.nickname(), data.role()));
		} catch (RuntimeException exception) {
			// 계약과 다른 손상된 JSON은 인증에 사용하지 않고 즉시 제거합니다.
			redisTemplate.delete(key);
			return Optional.empty();
		}
	}

	// sessionId에 해당하는 Redis 세션을 삭제합니다.
	public void delete(String sessionId) {
		// 로그아웃이나 회원 탈퇴가 끝나면 현재 세션을 즉시 사용할 수 없게 만듭니다.
		if (sessionId != null && sessionId.matches("[A-Za-z0-9_-]{43}")) {
			redisTemplate.delete(SESSION_KEY_PREFIX + sessionId);
		}
	}

	// SecureRandom 32바이트를 URL에 안전한 Base64 문자열(43자)로 인코딩해 세션 ID를 만듭니다.
	private String generateSessionId() {
		byte[] bytes = new byte[SESSION_ID_BYTES];
		secureRandom.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	public record SessionData(
		Long userId,
		String email,
		String nickname,
		String role,
		String createdAt
	) {
	}
}

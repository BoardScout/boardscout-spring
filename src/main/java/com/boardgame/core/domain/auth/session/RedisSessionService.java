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

@Service
@RequiredArgsConstructor
public class RedisSessionService {

	private static final String SESSION_KEY_PREFIX = "session:";
	private static final int SESSION_ID_BYTES = 32;

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private final AuthSessionProperties properties;
	private final SecureRandom secureRandom = new SecureRandom();

	public String create(User user) {
		String sessionId = generateSessionId();
		SessionData payload = new SessionData(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			user.getRole().name(),
			OffsetDateTime.now(ZoneOffset.UTC).toString());
		String json = objectMapper.writeValueAsString(payload);

		redisTemplate.opsForValue().set(SESSION_KEY_PREFIX + sessionId, json, properties.ttl());
		return sessionId;
	}

	public Optional<SessionPrincipal> findAndRefresh(String sessionId) {
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
			redisTemplate.expire(key, properties.ttl());
			return Optional.of(new SessionPrincipal(
				data.userId(), data.email(), data.nickname(), data.role()));
		} catch (RuntimeException exception) {
			redisTemplate.delete(key);
			return Optional.empty();
		}
	}

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

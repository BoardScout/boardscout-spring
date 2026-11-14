package com.boardgame.core.domain.auth.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;

import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.global.config.AuthSessionProperties;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class RedisSessionServiceTest {

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	private RedisSessionService redisSessionService;

	@BeforeEach
	void setUp() {
		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		AuthSessionProperties properties = new AuthSessionProperties("BGSESSION", Duration.ofMinutes(30), false);
		redisSessionService = new RedisSessionService(redisTemplate, new ObjectMapper(), properties);
	}

	@Test
	void storesPortableJsonSessionWithThirtyMinuteTtl() {
		User user = User.create("player@example.com", "encoded-password", "플레이어");
		ReflectionTestUtils.setField(user, "id", 1L);

		String sessionId = redisSessionService.create(user);

		ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
		verify(valueOperations).set(eq("session:" + sessionId), jsonCaptor.capture(), eq(Duration.ofMinutes(30)));
		assertThat(sessionId).hasSize(43).matches("[A-Za-z0-9_-]+");
		assertThat(jsonCaptor.getValue())
			.contains("\"userId\":1")
			.contains("\"email\":\"player@example.com\"")
			.contains("\"nickname\":\"플레이어\"")
			.contains("\"role\":\"USER\"")
			.contains("\"createdAt\":");
	}

	@Test
	void readsSessionAndRefreshesTtl() {
		String sessionId = "a".repeat(43);
		String json = """
			{"userId":1,"email":"player@example.com","nickname":"플레이어","role":"USER","createdAt":"2026-11-13T00:00:00Z"}
			""";
		given(valueOperations.get("session:" + sessionId)).willReturn(json);

		var principal = redisSessionService.findAndRefresh(sessionId);

		assertThat(principal).isPresent();
		assertThat(principal.orElseThrow().sessionId()).isEqualTo(sessionId);
		assertThat(principal.orElseThrow().userId()).isEqualTo(1L);
		assertThat(principal.orElseThrow().email()).isEqualTo("player@example.com");
		verify(redisTemplate).expire("session:" + sessionId, Duration.ofMinutes(30));
		assertThat(principal.orElseThrow().role()).isEqualTo("USER");
		verify(redisTemplate).expire("session:" + sessionId, Duration.ofMinutes(30));
	}

	@Test
	void deletesValidSession() {
		String sessionId = "a".repeat(43);

		redisSessionService.delete(sessionId);

		verify(redisTemplate).delete("session:" + sessionId);
	}
}

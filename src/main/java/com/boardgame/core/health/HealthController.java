package com.boardgame.core.health;

import java.time.OffsetDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 컨테이너와 역방향 프록시가 Spring 서버의 정상 동작 여부를 확인하는 API입니다.
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

	// 서비스 이름과 현재 서버 시간을 반환합니다.
	@GetMapping("/health")
	public HealthResponse health() {
		return new HealthResponse(
				"boardgame-core",
				"UP",
				OffsetDateTime.now().toString());
	}

	public record HealthResponse(String service, String status, String timestamp) {
	}
}


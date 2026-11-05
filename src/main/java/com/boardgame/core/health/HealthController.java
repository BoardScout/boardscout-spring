package com.boardgame.core.health;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 컨테이너와 역방향 프록시가 Spring 서버의 생존 여부를 확인하는 공개 API입니다.
 */
@RestController
@RequestMapping("/api/core")
public class HealthController {

	@GetMapping("/health")
	public ResponseEntity<Map<String, Object>> health() {
		return ResponseEntity.ok(Map.of(
				"service", "boardgame-core",
				"status", "UP",
				"timestamp", OffsetDateTime.now().toString()));
	}
}


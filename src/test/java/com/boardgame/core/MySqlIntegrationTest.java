package com.boardgame.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.boardgame.core.domain.boardgame.repository.BoardGameRepository;
import com.boardgame.core.domain.user.repository.UserRepository;

@Tag("mysql")
@ActiveProfiles("mysql-test")
@SpringBootTest
class MySqlIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BoardGameRepository boardGameRepository;

	@Test
	void connectsToMySqlAndValidatesTheMigratedSchema() {
		assertEquals(0, userRepository.count());
		// Flyway가 스키마 생성 뒤 추천 카탈로그 1,500개도 자동으로 적재해야 합니다.
		assertEquals(1500, boardGameRepository.count());
	}
}


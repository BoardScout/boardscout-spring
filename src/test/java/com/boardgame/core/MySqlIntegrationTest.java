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
		assertEquals(0, boardGameRepository.count());
	}
}


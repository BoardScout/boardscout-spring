package com.boardgame.core.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void returnsServiceHealth() throws Exception {
		mockMvc.perform(get("/api/core/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.service").value("boardgame-core"))
				.andExpect(jsonPath("$.status").value("UP"));
	}
}


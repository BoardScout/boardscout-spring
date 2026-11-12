package com.boardgame.core.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 회원가입과 로그인이 같은 방식으로 비밀번호를 해시하고 비교하도록 인코더를 제공합니다.
 * 사용자 엔티티에는 원문 대신 BCrypt 결과만 저장됩니다.
 */
@Configuration
public class PasswordConfig {

	// AuthService가 주입받아 회원가입에는 encode, 로그인에는 matches를 사용합니다.
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

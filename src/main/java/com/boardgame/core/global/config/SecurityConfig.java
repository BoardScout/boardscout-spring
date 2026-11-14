package com.boardgame.core.global.config;

import com.boardgame.core.domain.auth.security.RedisSessionAuthenticationFilter;
import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.global.exception.CommonErrorCode;
import com.boardgame.core.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import tools.jackson.databind.ObjectMapper;

/**
 * HTTP 요청이 컨트롤러에 도달하기 전 적용할 인증·인가 흐름을 조립합니다.
 * Spring HTTP 세션은 만들지 않고 BGSESSION 쿠키와 Redis JSON 세션을 인증 기준으로 사용합니다.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthSessionProperties.class)
public class SecurityConfig {

	private final ObjectMapper objectMapper;

	// 요청 쿠키를 Redis 세션과 대조해 SecurityContext를 구성하는 필터를 등록합니다.
	@Bean
	RedisSessionAuthenticationFilter redisSessionAuthenticationFilter(
		RedisSessionService redisSessionService,
		AuthSessionProperties sessionProperties
	) {
		return new RedisSessionAuthenticationFilter(redisSessionService, sessionProperties);
	}

	@Bean
	SecurityFilterChain securityFilterChain(
		HttpSecurity http,
		RedisSessionAuthenticationFilter redisSessionAuthenticationFilter
	) throws Exception {
		// 상태를 바꾸는 보호 API에서는 프런트가 읽을 수 있는 XSRF-TOKEN 쿠키 값을 헤더로 되돌려 보내야 합니다.
		// 로그인과 회원가입은 아직 인증 쿠키가 없는 진입점이므로 CSRF 검사 대상에서 제외합니다.
		CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

		http
			// 인증 상태는 Redis가 소유하므로 Spring의 HttpSession은 생성하지 않습니다.
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf
				.csrfTokenRepository(csrfTokenRepository)
				.ignoringRequestMatchers("/api/v1/auth/register", "/api/v1/auth/login"))
			// 폼 로그인·Basic 인증·기본 로그아웃 대신 JSON API와 커스텀 Redis 세션 방식을 사용합니다.
			.requestCache(requestCache -> requestCache.disable())
			.formLogin(formLogin -> formLogin.disable())
			.httpBasic(httpBasic -> httpBasic.disable())
			.logout(logout -> logout.disable())
			// 인증 진입점과 상태 확인은 공개하고, 나머지 core API는 유효한 세션이 있어야 통과시킵니다.
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/v1/auth/**", "/api/v1/health", "/actuator/health").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/board-games/**").permitAll()
				.requestMatchers("/api/v1/**").authenticated()
				.anyRequest().permitAll())
			// 인증되지 않은 보호 API 요청도 나머지 API와 같은 공통 오류 형식으로 반환합니다.
			.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint((request, response, exception) -> {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding("UTF-8");
				ApiResponse<Object> body = ApiResponse.failure(
					CommonErrorCode.UNAUTHORIZED.code(),
					CommonErrorCode.UNAUTHORIZED.message(),
					null);
				response.getWriter().write(objectMapper.writeValueAsString(body));
			}))
			// 인가 판단 전에 BGSESSION을 읽어 현재 요청의 Authentication을 구성합니다.
			.addFilterAfter(redisSessionAuthenticationFilter, LogoutFilter.class);

		return http.build();
	}
}

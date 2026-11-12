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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthSessionProperties.class)
public class SecurityConfig {

	private final ObjectMapper objectMapper;

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
		CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

		http
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf
				.csrfTokenRepository(csrfTokenRepository)
				.ignoringRequestMatchers("/api/core/auth/register", "/api/core/auth/login"))
			.requestCache(requestCache -> requestCache.disable())
			.formLogin(formLogin -> formLogin.disable())
			.httpBasic(httpBasic -> httpBasic.disable())
			.logout(logout -> logout.disable())
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/core/auth/**", "/api/core/health", "/actuator/health").permitAll()
				.requestMatchers("/api/core/**").authenticated()
				.anyRequest().permitAll())
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
			.addFilterAfter(redisSessionAuthenticationFilter, LogoutFilter.class);

		return http.build();
	}
}

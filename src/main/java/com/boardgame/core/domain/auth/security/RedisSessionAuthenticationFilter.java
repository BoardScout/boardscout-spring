package com.boardgame.core.domain.auth.security;

import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.global.config.AuthSessionProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * 로그인 이후 요청의 BGSESSION 쿠키를 Redis 세션과 대조하는 Spring Security 인증 필터입니다.
 * 유효한 세션이면 현재 요청에만 사용할 SecurityContext를 만들고 다음 필터로 넘깁니다.
 */
@RequiredArgsConstructor
public class RedisSessionAuthenticationFilter extends OncePerRequestFilter {

	private final RedisSessionService redisSessionService;
	private final AuthSessionProperties sessionProperties;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		// 앞선 인증 수단이 없는 요청만 쿠키 기반 인증을 시도합니다.
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			Cookie cookie = WebUtils.getCookie(request, sessionProperties.cookieName());
			if (cookie != null) {
				redisSessionService.findAndRefresh(cookie.getValue()).ifPresent(this::authenticate);
			}
		}

		filterChain.doFilter(request, response);
	}

	private void authenticate(SessionPrincipal principal) {
		// Redis에 저장된 역할을 Spring Security 권한 규칙(ROLE_*)으로 변환합니다.
		var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + principal.role()));
		var authentication = UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}

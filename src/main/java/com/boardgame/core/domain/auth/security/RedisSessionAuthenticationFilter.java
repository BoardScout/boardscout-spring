package com.boardgame.core.domain.auth.security;

import com.boardgame.core.domain.auth.session.RedisSessionService;
import com.boardgame.core.domain.auth.session.SessionProperties;
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

@RequiredArgsConstructor
public class RedisSessionAuthenticationFilter extends OncePerRequestFilter {

	private final RedisSessionService redisSessionService;
	private final SessionProperties sessionProperties;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			Cookie cookie = WebUtils.getCookie(request, sessionProperties.cookieName());
			if (cookie != null) {
				redisSessionService.findAndRefresh(cookie.getValue()).ifPresent(this::authenticate);
			}
		}

		filterChain.doFilter(request, response);
	}

	private void authenticate(SessionPrincipal principal) {
		var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + principal.role()));
		var authentication = UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}

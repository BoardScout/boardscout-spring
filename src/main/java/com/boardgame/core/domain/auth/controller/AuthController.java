package com.boardgame.core.domain.auth.controller;

import com.boardgame.core.domain.auth.dto.RegisterUserRequest;
import com.boardgame.core.domain.auth.dto.LoginRequest;
import com.boardgame.core.domain.auth.dto.LoginResult;
import com.boardgame.core.domain.auth.service.AuthService;
import com.boardgame.core.domain.auth.session.SessionCookieFactory;
import com.boardgame.core.domain.user.dto.UserResponse;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final SessionCookieFactory sessionCookieFactory;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public UserResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
		LoginResult result = authService.login(request);
		response.addHeader(HttpHeaders.SET_COOKIE, sessionCookieFactory.create(result.sessionId()).toString());
		return result.user();
	}
}

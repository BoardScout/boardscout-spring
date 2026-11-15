package com.boardgame.core.domain.user.controller;

import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.user.dto.UserPreferenceRequest;
import com.boardgame.core.domain.user.dto.UserPreferenceResponse;
import com.boardgame.core.domain.user.service.UserPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserPreferenceController {

	private final UserPreferenceService userPreferenceService;

	@PutMapping("/users/me/preferences")
	public UserPreferenceResponse saveMyPreferences(
		@AuthenticationPrincipal SessionPrincipal principal,
		@Valid @RequestBody UserPreferenceRequest request
	) {
		return userPreferenceService.save(principal.userId(), request);
	}

	@GetMapping("/users/me/preferences")
	public UserPreferenceResponse getMyPreferences(
		@AuthenticationPrincipal SessionPrincipal principal
	) {
		return userPreferenceService.get(principal.userId());
	}

	@GetMapping("/internal/users/{userId}/preferences")
	public UserPreferenceResponse getPreferencesForRecommendation(@PathVariable Long userId) {
		return userPreferenceService.get(userId);
	}
}

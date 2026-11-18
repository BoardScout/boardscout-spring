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

	// 로그인한 회원의 추천 취향을 저장하거나 덮어씁니다.
	@PutMapping("/users/me/preferences")
	public UserPreferenceResponse saveMyPreferences(
		@AuthenticationPrincipal SessionPrincipal principal,
		@Valid @RequestBody UserPreferenceRequest request
	) {
		return userPreferenceService.save(principal.userId(), request);
	}

	// 로그인한 회원이 저장해 둔 추천 취향을 조회합니다.
	@GetMapping("/users/me/preferences")
	public UserPreferenceResponse getMyPreferences(
		@AuthenticationPrincipal SessionPrincipal principal
	) {
		return userPreferenceService.get(principal.userId());
	}

	// 추천 서버가 회원 ID로 저장된 취향을 조회할 때 사용하는 내부용 API입니다.
	@GetMapping("/internal/users/{userId}/preferences")
	public UserPreferenceResponse getPreferencesForRecommendation(@PathVariable Long userId) {
		return userPreferenceService.get(userId);
	}
}

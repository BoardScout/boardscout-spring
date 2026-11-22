package com.boardgame.core.domain.boardgame.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.boardgame.dto.UserFavoriteGameResponse;
import com.boardgame.core.domain.boardgame.service.UserFavoriteGameService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users/me/favorites")
@RequiredArgsConstructor
public class UserFavoriteGameController {

	private final UserFavoriteGameService favoriteService;

	// 로그인한 사용자의 관심 게임 전체를 조회합니다.
	@GetMapping
	public List<UserFavoriteGameResponse> getAll(
		@AuthenticationPrincipal SessionPrincipal principal
	) {
		return favoriteService.getAll(principal.userId());
	}

	// 데이터셋 게임 하나를 관심 등록합니다. 여러 번 호출해도 한 건만 남습니다.
	@PutMapping("/{datasetItemId}")
	public UserFavoriteGameResponse add(
		@AuthenticationPrincipal SessionPrincipal principal,
		@PathVariable Integer datasetItemId
	) {
		return favoriteService.add(principal.userId(), datasetItemId);
	}

	// 데이터셋 게임 하나의 관심 등록을 해제합니다.
	@DeleteMapping("/{datasetItemId}")
	public void remove(
		@AuthenticationPrincipal SessionPrincipal principal,
		@PathVariable Integer datasetItemId
	) {
		favoriteService.remove(principal.userId(), datasetItemId);
	}
}

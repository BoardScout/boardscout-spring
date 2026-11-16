package com.boardgame.core.domain.rating.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardgame.core.domain.auth.security.SessionPrincipal;
import com.boardgame.core.domain.rating.dto.UserGameRatingRequest;
import com.boardgame.core.domain.rating.dto.UserGameRatingResponse;
import com.boardgame.core.domain.rating.service.UserGameRatingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users/me/ratings")
@RequiredArgsConstructor
public class UserGameRatingController {

	private final UserGameRatingService ratingService;

	@GetMapping
	public List<UserGameRatingResponse> getAll(
		@AuthenticationPrincipal SessionPrincipal principal
	) {
		return ratingService.getAll(principal.userId());
	}

	@PutMapping("/{datasetItemId}")
	public UserGameRatingResponse save(
		@AuthenticationPrincipal SessionPrincipal principal,
		@PathVariable Integer datasetItemId,
		@Valid @RequestBody UserGameRatingRequest request
	) {
		return ratingService.save(principal.userId(), datasetItemId, request);
	}

	@DeleteMapping("/{datasetItemId}")
	public void delete(
		@AuthenticationPrincipal SessionPrincipal principal,
		@PathVariable Integer datasetItemId
	) {
		ratingService.delete(principal.userId(), datasetItemId);
	}
}

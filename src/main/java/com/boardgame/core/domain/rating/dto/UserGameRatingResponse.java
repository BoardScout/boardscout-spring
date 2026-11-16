package com.boardgame.core.domain.rating.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.boardgame.core.domain.rating.entity.UserGameRating;

public record UserGameRatingResponse(
	Integer datasetItemId,
	BigDecimal rating,
	LocalDateTime updatedAt
) {
	public static UserGameRatingResponse from(UserGameRating entity) {
		return new UserGameRatingResponse(
			entity.getBoardGame().getDatasetItemId(),
			entity.getRating(),
			entity.getUpdatedAt());
	}
}

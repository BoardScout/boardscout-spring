package com.boardgame.core.domain.rating.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.boardgame.core.domain.rating.entity.UserGameRating;

public record UserGameRatingResponse(
	Integer datasetItemId,
	BigDecimal rating,
	LocalDateTime updatedAt
) {
	// 별점 엔티티에서 데이터셋 게임 ID, 점수, 수정 시각만 응답으로 옮깁니다.
	public static UserGameRatingResponse from(UserGameRating entity) {
		return new UserGameRatingResponse(
			entity.getBoardGame().getDatasetItemId(),
			entity.getRating(),
			entity.getUpdatedAt());
	}
}

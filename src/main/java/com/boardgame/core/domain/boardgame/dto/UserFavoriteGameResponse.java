package com.boardgame.core.domain.boardgame.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.boardgame.entity.UserFavoriteGame;

public record UserFavoriteGameResponse(
	Integer datasetItemId,
	Long bggId,
	String nameEn,
	String nameKo,
	String category,
	int minPlayers,
	int maxPlayers,
	int maxPlayTimeMinutes,
	BigDecimal difficulty,
	LocalDateTime createdAt
) {
	// 마이페이지 목록에 필요한 게임 정보와 등록 시각을 응답으로 옮깁니다. 필드 이름은 카탈로그 응답과 같습니다.
	public static UserFavoriteGameResponse from(UserFavoriteGame entity) {
		BoardGame game = entity.getBoardGame();
		return new UserFavoriteGameResponse(
			game.getDatasetItemId(), game.getBggId(), game.getName(), game.getNameKo(), game.getCategory(),
			game.getMinPlayers(), game.getMaxPlayers(), game.getMaxPlayTimeMinutes(), game.getDifficulty(),
			entity.getCreatedAt());
	}
}

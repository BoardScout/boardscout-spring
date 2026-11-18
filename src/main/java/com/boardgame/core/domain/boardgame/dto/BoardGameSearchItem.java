package com.boardgame.core.domain.boardgame.dto;

import java.math.BigDecimal;

import com.boardgame.core.domain.boardgame.entity.BoardGame;

public record BoardGameSearchItem(
	Long id,
	Integer datasetItemId,
	Long bggId,
	String nameEn,
	String nameKo,
	String descriptionKo,
	String imageUrl,
	String thumbnailUrl,
	String category,
	int minPlayers,
	int maxPlayers,
	int minPlayTimeMinutes,
	int maxPlayTimeMinutes,
	Integer minAge,
	Integer yearPublished,
	BigDecimal difficulty,
	BigDecimal averageRating
) {

	// 보드게임 엔티티에서 검색 결과 한 건에 필요한 필드만 응답으로 옮깁니다.
	public static BoardGameSearchItem from(BoardGame game) {
		return new BoardGameSearchItem(
			game.getId(), game.getDatasetItemId(), game.getBggId(), game.getName(), game.getNameKo(),
			game.getDescription(), game.getImageUrl(), game.getThumbnailUrl(), game.getCategory(), game.getMinPlayers(),
			game.getMaxPlayers(), game.getMinPlayTimeMinutes(), game.getMaxPlayTimeMinutes(),
			game.getMinAge(), game.getYearPublished(), game.getDifficulty(), game.getAverageRating());
	}
}

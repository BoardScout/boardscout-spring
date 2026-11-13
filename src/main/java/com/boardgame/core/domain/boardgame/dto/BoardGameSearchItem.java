package com.boardgame.core.domain.boardgame.dto;

import java.math.BigDecimal;

import com.boardgame.core.domain.boardgame.entity.BoardGame;

public record BoardGameSearchItem(
	Long id,
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

	public static BoardGameSearchItem from(BoardGame game) {
		return new BoardGameSearchItem(
			game.getId(), game.getBggId(), game.getName(), game.getNameKo(), game.getDescription(),
			game.getImageUrl(), game.getThumbnailUrl(), game.getCategory(), game.getMinPlayers(),
			game.getMaxPlayers(), game.getMinPlayTimeMinutes(), game.getMaxPlayTimeMinutes(),
			game.getMinAge(), game.getYearPublished(), game.getDifficulty(), game.getAverageRating());
	}
}

package com.boardgame.core.domain.boardgame.dto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

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
	List<String> themes,
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
			game.getDescription(), game.getImageUrl(), game.getThumbnailUrl(), game.getCategory(),
			splitThemes(game.getThemes()), game.getMinPlayers(),
			game.getMaxPlayers(), game.getMinPlayTimeMinutes(), game.getMaxPlayTimeMinutes(),
			game.getMinAge(), game.getYearPublished(), game.getDifficulty(), game.getAverageRating());
	}

	// 쉼표로 저장된 테마 코드를 목록으로 나눕니다. 값이 없으면 빈 목록입니다.
	private static List<String> splitThemes(String themes) {
		if (themes == null || themes.isBlank()) {
			return List.of();
		}
		return Arrays.asList(themes.split(","));
	}
}

package com.boardgame.core.domain.user.dto;

import java.util.List;

import com.boardgame.core.domain.user.entity.PreferencePlayStyle;
import com.boardgame.core.domain.user.entity.PreferenceTheme;
import com.boardgame.core.domain.user.entity.RecommendationMode;
import com.boardgame.core.domain.user.entity.UserPreference;

public record UserPreferenceResponse(
	RecommendationMode recommendationMode,
	Integer playerCount,
	Integer maxPlayTime,
	Integer difficulty,
	List<PreferenceTheme> themes,
	List<PreferencePlayStyle> playStyles,
	List<Long> likedGameIds
) {
	public static UserPreferenceResponse from(UserPreference preference) {
		return new UserPreferenceResponse(
			preference.getRecommendationMode(),
			preference.getPlayerCount(),
			preference.getMaxPlayTime(),
			preference.getDifficulty(),
			List.copyOf(preference.getThemes()),
			List.copyOf(preference.getPlayStyles()),
			List.copyOf(preference.getLikedGameIds()));
	}
}

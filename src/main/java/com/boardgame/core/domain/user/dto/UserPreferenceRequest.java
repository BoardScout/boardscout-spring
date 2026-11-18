package com.boardgame.core.domain.user.dto;

import java.util.HashSet;
import java.util.List;

import com.boardgame.core.domain.user.entity.PreferencePlayStyle;
import com.boardgame.core.domain.user.entity.PreferenceTheme;
import com.boardgame.core.domain.user.entity.RecommendationMode;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UserPreferenceRequest(
	@NotNull(message = "추천 모드를 선택해 주세요.")
	RecommendationMode recommendationMode,
	@Min(value = 1, message = "인원은 1명 이상이어야 합니다.")
	@Max(value = 100, message = "인원은 100명 이하여야 합니다.")
	Integer playerCount,
	@Min(value = 10, message = "플레이 시간은 10분 이상이어야 합니다.")
	@Max(value = 360, message = "플레이 시간은 360분 이하여야 합니다.")
	Integer maxPlayTime,
	@Min(value = 1, message = "난이도는 1 이상이어야 합니다.")
	@Max(value = 3, message = "난이도는 3 이하여야 합니다.")
	Integer difficulty,
	@Size(max = 8, message = "테마는 8개 이하로 선택해 주세요.")
	List<PreferenceTheme> themes,
	@Size(max = 6, message = "플레이 방식은 6개 이하로 선택해 주세요.")
	List<PreferencePlayStyle> playStyles,
	@Size(max = 20, message = "좋아한 게임은 20개 이하로 선택해 주세요.")
	List<@PositiveOrZero(message = "게임 ID는 0 이상이어야 합니다.") Long> likedGameIds
) {
	// 목록 필드가 비어 있으면 빈 목록으로, 있으면 복사본으로 바꿔 불변 상태로 보관합니다.
	public UserPreferenceRequest {
		themes = themes == null ? List.of() : List.copyOf(themes);
		playStyles = playStyles == null ? List.of() : List.copyOf(playStyles);
		likedGameIds = likedGameIds == null ? List.of() : List.copyOf(likedGameIds);
	}

	// 상황 추천 모드에서는 인원, 플레이 시간, 난이도가 모두 입력됐는지 확인합니다.
	@AssertTrue(message = "상황 추천에는 인원, 플레이 시간, 난이도가 필요합니다.")
	public boolean isContextPreferenceComplete() {
		return recommendationMode != RecommendationMode.CONTEXT
			|| (playerCount != null && maxPlayTime != null && difficulty != null);
	}

	// 취향 추천 모드에서는 중복 없는 게임이 3개 이상 선택됐는지 확인합니다.
	@AssertTrue(message = "취향 추천에는 서로 다른 게임을 3개 이상 선택해야 합니다.")
	public boolean isTastePreferenceComplete() {
		return recommendationMode != RecommendationMode.TASTE
			|| (likedGameIds.size() >= 3 && new HashSet<>(likedGameIds).size() == likedGameIds.size());
	}
}

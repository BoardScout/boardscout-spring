package com.boardgame.core.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_preferences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPreference {

	@Id
	@Column(name = "user_id")
	private Long userId;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "recommendation_mode", nullable = false, length = 20)
	private RecommendationMode recommendationMode;

	@Column(name = "player_count")
	private Integer playerCount;

	@Column(name = "max_play_time")
	private Integer maxPlayTime;

	@Column
	private Integer difficulty;

	@ElementCollection
	@CollectionTable(name = "user_preference_themes", joinColumns = @JoinColumn(name = "user_id"))
	@OrderColumn(name = "sort_order")
	@Enumerated(EnumType.STRING)
	@Column(name = "theme", nullable = false, length = 20)
	private List<PreferenceTheme> themes = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "user_preference_play_styles", joinColumns = @JoinColumn(name = "user_id"))
	@OrderColumn(name = "sort_order")
	@Enumerated(EnumType.STRING)
	@Column(name = "play_style", nullable = false, length = 20)
	private List<PreferencePlayStyle> playStyles = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "user_preference_liked_games", joinColumns = @JoinColumn(name = "user_id"))
	@OrderColumn(name = "sort_order")
	@Column(name = "game_id", nullable = false)
	private List<Long> likedGameIds = new ArrayList<>();

	private UserPreference(User user) {
		this.user = user;
	}

	public static UserPreference create(User user) {
		return new UserPreference(user);
	}

	public void update(
		RecommendationMode recommendationMode,
		Integer playerCount,
		Integer maxPlayTime,
		Integer difficulty,
		List<PreferenceTheme> themes,
		List<PreferencePlayStyle> playStyles,
		List<Long> likedGameIds
	) {
		this.recommendationMode = recommendationMode;
		this.playerCount = playerCount;
		this.maxPlayTime = maxPlayTime;
		this.difficulty = difficulty;
		this.themes.clear();
		this.themes.addAll(themes);
		this.playStyles.clear();
		this.playStyles.addAll(playStyles);
		this.likedGameIds.clear();
		this.likedGameIds.addAll(likedGameIds);
	}
}

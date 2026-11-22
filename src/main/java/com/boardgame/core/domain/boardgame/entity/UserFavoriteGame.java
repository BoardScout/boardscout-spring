package com.boardgame.core.domain.boardgame.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "user_favorite_games",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_user_favorite_games_user_game",
		columnNames = {"user_id", "board_game_id"}
	)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFavoriteGame {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "board_game_id", nullable = false)
	private BoardGame boardGame;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// 검증은 정적 팩토리에서 마치고, 생성자는 연관만 연결합니다.
	private UserFavoriteGame(User user, BoardGame boardGame) {
		this.user = user;
		this.boardGame = boardGame;
	}

	// 사용자와 추천 데이터셋에 속한 게임을 연결해 관심 등록을 새로 만듭니다.
	public static UserFavoriteGame create(User user, BoardGame boardGame) {
		if (user == null || boardGame == null) {
			throw new IllegalArgumentException("user and boardGame are required");
		}
		if (boardGame.getDatasetItemId() == null) {
			throw new IllegalArgumentException("dataset game is required");
		}
		return new UserFavoriteGame(user, boardGame);
	}
}

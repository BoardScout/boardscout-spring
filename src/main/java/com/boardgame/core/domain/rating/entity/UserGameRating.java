package com.boardgame.core.domain.rating.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
	name = "user_game_ratings",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_user_game_ratings_user_game",
		columnNames = {"user_id", "board_game_id"}
	)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGameRating {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "board_game_id", nullable = false)
	private BoardGame boardGame;

	@Column(nullable = false, precision = 4, scale = 2)
	private BigDecimal rating;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	private UserGameRating(User user, BoardGame boardGame, BigDecimal rating) {
		this.user = user;
		this.boardGame = boardGame;
		update(rating);
	}

	public static UserGameRating create(User user, BoardGame boardGame, BigDecimal rating) {
		if (user == null || boardGame == null) {
			throw new IllegalArgumentException("user and boardGame are required");
		}
		if (boardGame.getDatasetItemId() == null) {
			throw new IllegalArgumentException("dataset game is required");
		}
		return new UserGameRating(user, boardGame, rating);
	}

	public void update(BigDecimal rating) {
		if (rating == null
			|| rating.compareTo(BigDecimal.ONE) < 0
			|| rating.compareTo(BigDecimal.TEN) > 0) {
			throw new IllegalArgumentException("rating must be between 1 and 10");
		}
		this.rating = rating;
	}
}

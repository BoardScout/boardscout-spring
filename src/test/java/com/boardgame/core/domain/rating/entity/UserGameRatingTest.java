package com.boardgame.core.domain.rating.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.user.entity.User;

class UserGameRatingTest {

	@Test
	void acceptsBggTenPointScaleBoundaries() {
		UserGameRating rating = UserGameRating.create(user(), game(), BigDecimal.ONE);

		assertThat(rating.getRating()).isEqualByComparingTo("1");
		rating.update(BigDecimal.TEN);
		assertThat(rating.getRating()).isEqualByComparingTo("10");
	}

	@Test
	void rejectsRatingOutsideBggScale() {
		assertThatThrownBy(() -> UserGameRating.create(user(), game(), BigDecimal.ZERO))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> UserGameRating.create(user(), game(), new BigDecimal("10.01")))
			.isInstanceOf(IllegalArgumentException.class);
	}

	private User user() {
		return User.create("player@example.com", "encoded-password", "플레이어");
	}

	private BoardGame game() {
		return BoardGame.fromDataset(
			15, 230802L, "Azul", null, "Abstract", 2, 4, 30, 45,
			8, 2017, new BigDecimal("1.7639"), new BigDecimal("7.79966"), Set.of(), Set.of());
	}
}

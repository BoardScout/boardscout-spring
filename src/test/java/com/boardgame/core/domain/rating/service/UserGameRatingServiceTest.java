package com.boardgame.core.domain.rating.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.boardgame.repository.BoardGameRepository;
import com.boardgame.core.domain.rating.dto.UserGameRatingRequest;
import com.boardgame.core.domain.rating.dto.UserGameRatingResponse;
import com.boardgame.core.domain.rating.entity.UserGameRating;
import com.boardgame.core.domain.rating.repository.UserGameRatingRepository;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserGameRatingServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private BoardGameRepository boardGameRepository;
	@Mock
	private UserGameRatingRepository ratingRepository;

	private UserGameRatingService service;

	@BeforeEach
	void setUp() {
		service = new UserGameRatingService(userRepository, boardGameRepository, ratingRepository);
	}

	@Test
	void createsAndReturnsRatingByDatasetItemId() {
		User user = User.create("player@example.com", "encoded-password", "플레이어");
		BoardGame game = game();
		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(boardGameRepository.findByDatasetItemId(15)).willReturn(Optional.of(game));
		given(ratingRepository.findByUserAndBoardGame(user, game)).willReturn(Optional.empty());
		given(ratingRepository.save(any(UserGameRating.class)))
			.willAnswer(invocation -> invocation.getArgument(0));

		UserGameRatingResponse response = service.save(
			1L, 15, new UserGameRatingRequest(new BigDecimal("8.5")));

		assertThat(response.datasetItemId()).isEqualTo(15);
		assertThat(response.rating()).isEqualByComparingTo("8.5");
	}

	@Test
	void updatesExistingRatingInsteadOfCreatingDuplicate() {
		User user = User.create("player@example.com", "encoded-password", "플레이어");
		BoardGame game = game();
		UserGameRating existing = UserGameRating.create(user, game, new BigDecimal("6"));
		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(boardGameRepository.findByDatasetItemId(15)).willReturn(Optional.of(game));
		given(ratingRepository.findByUserAndBoardGame(user, game)).willReturn(Optional.of(existing));
		given(ratingRepository.save(existing)).willReturn(existing);

		UserGameRatingResponse response = service.save(
			1L, 15, new UserGameRatingRequest(new BigDecimal("9")));

		assertThat(response.rating()).isEqualByComparingTo("9");
	}

	private BoardGame game() {
		return BoardGame.fromDataset(
			15, 230802L, "Azul", null, "Abstract", 2, 4, 30, 45,
			8, 2017, new BigDecimal("1.7639"), new BigDecimal("7.79966"), Set.of(), Set.of());
	}
}

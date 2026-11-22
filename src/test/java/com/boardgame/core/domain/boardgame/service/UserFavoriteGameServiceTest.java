package com.boardgame.core.domain.boardgame.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
import com.boardgame.core.domain.boardgame.dto.UserFavoriteGameResponse;
import com.boardgame.core.domain.boardgame.entity.UserFavoriteGame;
import com.boardgame.core.domain.boardgame.exception.FavoriteErrorCode;
import com.boardgame.core.domain.boardgame.repository.UserFavoriteGameRepository;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class UserFavoriteGameServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private BoardGameRepository boardGameRepository;
	@Mock
	private UserFavoriteGameRepository favoriteRepository;

	private UserFavoriteGameService service;

	@BeforeEach
	void setUp() {
		service = new UserFavoriteGameService(userRepository, boardGameRepository, favoriteRepository);
	}

	@Test
	void addsFavoriteByDatasetItemId() {
		User user = user();
		BoardGame game = game();
		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(boardGameRepository.findByDatasetItemId(15)).willReturn(Optional.of(game));
		given(favoriteRepository.findByUserAndBoardGame(user, game)).willReturn(Optional.empty());
		given(favoriteRepository.save(any(UserFavoriteGame.class)))
			.willAnswer(invocation -> invocation.getArgument(0));

		UserFavoriteGameResponse response = service.add(1L, 15);

		assertThat(response.datasetItemId()).isEqualTo(15);
		assertThat(response.nameEn()).isEqualTo("Azul");
	}

	@Test
	void addingSameGameTwiceKeepsExistingFavorite() {
		User user = user();
		BoardGame game = game();
		UserFavoriteGame existing = UserFavoriteGame.create(user, game);
		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(boardGameRepository.findByDatasetItemId(15)).willReturn(Optional.of(game));
		given(favoriteRepository.findByUserAndBoardGame(user, game)).willReturn(Optional.of(existing));

		service.add(1L, 15);

		verify(favoriteRepository, never()).save(any());
	}

	@Test
	void removingGameThatIsNotFavoriteDoesNothing() {
		User user = user();
		BoardGame game = game();
		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(boardGameRepository.findByDatasetItemId(15)).willReturn(Optional.of(game));
		given(favoriteRepository.findByUserAndBoardGame(user, game)).willReturn(Optional.empty());

		service.remove(1L, 15);

		verify(favoriteRepository, never()).delete(any());
	}

	@Test
	void rejectsUnknownDatasetGame() {
		given(userRepository.findById(1L)).willReturn(Optional.of(user()));
		given(boardGameRepository.findByDatasetItemId(9999)).willReturn(Optional.empty());

		assertThatThrownBy(() -> service.add(1L, 9999))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(FavoriteErrorCode.DATASET_GAME_NOT_FOUND);
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

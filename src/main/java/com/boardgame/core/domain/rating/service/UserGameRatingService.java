package com.boardgame.core.domain.rating.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.boardgame.repository.BoardGameRepository;
import com.boardgame.core.domain.rating.dto.UserGameRatingRequest;
import com.boardgame.core.domain.rating.dto.UserGameRatingResponse;
import com.boardgame.core.domain.rating.entity.UserGameRating;
import com.boardgame.core.domain.rating.exception.RatingErrorCode;
import com.boardgame.core.domain.rating.repository.UserGameRatingRepository;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.exception.UserErrorCode;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserGameRatingService {

	private final UserRepository userRepository;
	private final BoardGameRepository boardGameRepository;
	private final UserGameRatingRepository ratingRepository;

	@Transactional
	public UserGameRatingResponse save(Long userId, Integer datasetItemId, UserGameRatingRequest request) {
		User user = findUser(userId);
		BoardGame game = findDatasetGame(datasetItemId);
		UserGameRating rating = ratingRepository.findByUserAndBoardGame(user, game)
			.orElseGet(() -> UserGameRating.create(user, game, request.rating()));
		rating.update(request.rating());
		return UserGameRatingResponse.from(ratingRepository.save(rating));
	}

	@Transactional(readOnly = true)
	public List<UserGameRatingResponse> getAll(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}
		return ratingRepository.findAllByUserIdOrderByUpdatedAtDesc(userId).stream()
			.map(UserGameRatingResponse::from)
			.toList();
	}

	@Transactional
	public void delete(Long userId, Integer datasetItemId) {
		User user = findUser(userId);
		BoardGame game = findDatasetGame(datasetItemId);
		UserGameRating rating = ratingRepository.findByUserAndBoardGame(user, game)
			.orElseThrow(() -> new BusinessException(RatingErrorCode.RATING_NOT_FOUND));
		ratingRepository.delete(rating);
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
	}

	private BoardGame findDatasetGame(Integer datasetItemId) {
		return boardGameRepository.findByDatasetItemId(datasetItemId)
			.orElseThrow(() -> new BusinessException(RatingErrorCode.DATASET_GAME_NOT_FOUND));
	}
}

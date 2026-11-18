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

	// 데이터셋 게임에 남긴 별점이 없으면 새로 만들고, 있으면 점수를 바꿔 저장합니다.
	@Transactional
	public UserGameRatingResponse save(Long userId, Integer datasetItemId, UserGameRatingRequest request) {
		User user = findUser(userId);
		BoardGame game = findDatasetGame(datasetItemId);
		UserGameRating rating = ratingRepository.findByUserAndBoardGame(user, game)
			.orElseGet(() -> UserGameRating.create(user, game, request.rating()));
		rating.update(request.rating());
		return UserGameRatingResponse.from(ratingRepository.save(rating));
	}

	// 사용자의 별점 목록을 최근 수정순으로 반환합니다. 없는 사용자는 오류로 처리합니다.
	@Transactional(readOnly = true)
	public List<UserGameRatingResponse> getAll(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}
		return ratingRepository.findAllByUserIdOrderByUpdatedAtDesc(userId).stream()
			.map(UserGameRatingResponse::from)
			.toList();
	}

	// 사용자가 남긴 별점을 삭제합니다. 저장된 별점이 없으면 오류로 처리합니다.
	@Transactional
	public void delete(Long userId, Integer datasetItemId) {
		User user = findUser(userId);
		BoardGame game = findDatasetGame(datasetItemId);
		UserGameRating rating = ratingRepository.findByUserAndBoardGame(user, game)
			.orElseThrow(() -> new BusinessException(RatingErrorCode.RATING_NOT_FOUND));
		ratingRepository.delete(rating);
	}

	// 회원 ID로 사용자를 조회하고 없으면 오류를 던집니다.
	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
	}

	// 추천 데이터셋의 게임 ID로 보드게임을 조회하고 없으면 오류를 던집니다.
	private BoardGame findDatasetGame(Integer datasetItemId) {
		// 추천 API와 별점 API가 같은 게임을 가리키도록 추천 데이터셋의 ID로 조회합니다.
		return boardGameRepository.findByDatasetItemId(datasetItemId)
			.orElseThrow(() -> new BusinessException(RatingErrorCode.DATASET_GAME_NOT_FOUND));
	}
}

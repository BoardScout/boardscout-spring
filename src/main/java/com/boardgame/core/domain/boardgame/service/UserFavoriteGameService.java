package com.boardgame.core.domain.boardgame.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.boardgame.repository.BoardGameRepository;
import com.boardgame.core.domain.boardgame.dto.UserFavoriteGameResponse;
import com.boardgame.core.domain.boardgame.entity.UserFavoriteGame;
import com.boardgame.core.domain.boardgame.exception.FavoriteErrorCode;
import com.boardgame.core.domain.boardgame.repository.UserFavoriteGameRepository;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.exception.UserErrorCode;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFavoriteGameService {

	private final UserRepository userRepository;
	private final BoardGameRepository boardGameRepository;
	private final UserFavoriteGameRepository favoriteRepository;

	// 사용자의 관심 게임을 최근 등록순으로 반환합니다. 없는 사용자는 오류로 처리합니다.
	@Transactional(readOnly = true)
	public List<UserFavoriteGameResponse> getAll(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}
		return favoriteRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
			.map(UserFavoriteGameResponse::from)
			.toList();
	}

	// 게임을 관심 등록합니다. 이미 등록한 게임이면 새로 만들지 않고 기존 등록을 그대로 반환합니다.
	@Transactional
	public UserFavoriteGameResponse add(Long userId, Integer datasetItemId) {
		User user = findUser(userId);
		BoardGame game = findDatasetGame(datasetItemId);
		UserFavoriteGame favorite = favoriteRepository.findByUserAndBoardGame(user, game)
			.orElseGet(() -> favoriteRepository.save(UserFavoriteGame.create(user, game)));
		return UserFavoriteGameResponse.from(favorite);
	}

	// 관심 등록을 해제합니다. 등록되지 않은 게임이어도 결과가 같으므로 오류 없이 끝냅니다.
	@Transactional
	public void remove(Long userId, Integer datasetItemId) {
		User user = findUser(userId);
		BoardGame game = findDatasetGame(datasetItemId);
		favoriteRepository.findByUserAndBoardGame(user, game).ifPresent(favoriteRepository::delete);
	}

	// 회원 ID로 사용자를 조회하고 없으면 오류를 던집니다.
	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
	}

	// 별점과 같은 기준으로 추천 데이터셋의 게임 ID로 보드게임을 조회하고 없으면 오류를 던집니다.
	private BoardGame findDatasetGame(Integer datasetItemId) {
		return boardGameRepository.findByDatasetItemId(datasetItemId)
			.orElseThrow(() -> new BusinessException(FavoriteErrorCode.DATASET_GAME_NOT_FOUND));
	}
}

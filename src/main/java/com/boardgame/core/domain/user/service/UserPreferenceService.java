package com.boardgame.core.domain.user.service;

import com.boardgame.core.domain.user.dto.UserPreferenceRequest;
import com.boardgame.core.domain.user.dto.UserPreferenceResponse;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.entity.UserPreference;
import com.boardgame.core.domain.user.exception.UserErrorCode;
import com.boardgame.core.domain.user.repository.UserPreferenceRepository;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

	private final UserRepository userRepository;
	private final UserPreferenceRepository userPreferenceRepository;

	// 회원의 취향이 없으면 새로 만들고, 있으면 요청 내용으로 덮어써 저장합니다.
	@Transactional
	public UserPreferenceResponse save(Long userId, UserPreferenceRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		UserPreference preference = userPreferenceRepository.findById(userId)
			.orElseGet(() -> UserPreference.create(user));

		preference.update(
			request.recommendationMode(),
			request.playerCount(),
			request.maxPlayTime(),
			request.difficulty(),
			request.themes(),
			request.playStyles(),
			request.likedGameIds());

		return UserPreferenceResponse.from(userPreferenceRepository.save(preference));
	}

	// 저장된 취향을 조회합니다. 저장된 값이 없으면 오류로 처리합니다.
	@Transactional(readOnly = true)
	public UserPreferenceResponse get(Long userId) {
		return userPreferenceRepository.findById(userId)
			.map(UserPreferenceResponse::from)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_PREFERENCE_NOT_FOUND));
	}
}

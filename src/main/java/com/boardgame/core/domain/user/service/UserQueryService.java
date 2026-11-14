package com.boardgame.core.domain.user.service;

import com.boardgame.core.domain.user.dto.UserResponse;
import com.boardgame.core.domain.user.exception.UserErrorCode;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public UserResponse getById(Long userId) {
		// 세션의 사용자 ID로 DB를 다시 조회해 탈퇴했거나 없는 계정을 걸러냅니다.
		return userRepository.findById(userId)
			.map(UserResponse::from)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
	}
}

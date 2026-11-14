package com.boardgame.core.domain.user.service;

import com.boardgame.core.domain.user.dto.UpdateUserRequest;
import com.boardgame.core.domain.user.dto.UserResponse;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.exception.UserErrorCode;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService {

	private final UserRepository userRepository;

	@Transactional
	public UserResponse update(Long userId, UpdateUserRequest request) {
		// 1. 인증된 사용자 ID로 수정 대상 엔티티를 조회합니다.
		User user = getUser(userId);

		// 2. 엔티티 메서드가 값 검증과 상태 변경을 함께 담당합니다.
		user.updateNickname(request.nickname().strip());

		// 3. JPA 변경 감지가 트랜잭션 종료 시 UPDATE 쿼리를 실행합니다.
		return UserResponse.from(user);
	}

	@Transactional
	public void delete(Long userId) {
		// 1. 없는 회원 탈퇴를 성공처럼 처리하지 않도록 먼저 조회합니다.
		User user = getUser(userId);

		// 2. 영속 엔티티를 삭제하면 트랜잭션 종료 시 DELETE 쿼리가 실행됩니다.
		userRepository.delete(user);
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
	}
}

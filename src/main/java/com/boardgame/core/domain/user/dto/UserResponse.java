package com.boardgame.core.domain.user.dto;

import com.boardgame.core.domain.user.entity.User;

public record UserResponse(Long id, String email, String nickname) {

	// 회원 엔티티에서 비밀번호 해시 등을 제외한 공개 필드만 응답으로 옮깁니다.
	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
	}
}

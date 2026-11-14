package com.boardgame.core.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 로그인한 회원이 직접 수정할 수 있는 최소 프로필 정보입니다. */
public record UpdateUserRequest(
	@NotBlank(message = "닉네임을 입력해 주세요.")
	@Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
	String nickname
) {
}

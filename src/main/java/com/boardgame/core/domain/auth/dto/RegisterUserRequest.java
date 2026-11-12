package com.boardgame.core.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
	@NotBlank(message = "이메일을 입력해 주세요.")
	@Email(message = "올바른 이메일 형식으로 입력해 주세요.")
	@Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
	String email,

	@NotBlank(message = "닉네임을 입력해 주세요.")
	@Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
	String nickname,

	@NotBlank(message = "비밀번호를 입력해 주세요.")
	@Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하여야 합니다.")
	String password,

	@NotBlank(message = "비밀번호 확인을 입력해 주세요.")
	String passwordConfirm
) {

	@JsonIgnore
	@AssertTrue(message = "비밀번호가 서로 다릅니다.")
	public boolean isPasswordConfirmed() {
		return password != null && password.equals(passwordConfirm);
	}
}

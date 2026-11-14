package com.boardgame.core.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.boardgame.core.domain.user.dto.UpdateUserRequest;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.exception.UserErrorCode;
import com.boardgame.core.domain.user.repository.UserRepository;
import com.boardgame.core.global.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserCommandService userCommandService;

	@Test
	void updatesNicknameAfterTrimmingWhitespace() {
		User user = persistedUser();
		given(userRepository.findById(1L)).willReturn(Optional.of(user));

		var response = userCommandService.update(1L, new UpdateUserRequest(" 새 닉네임 "));

		assertThat(response.nickname()).isEqualTo("새 닉네임");
		assertThat(user.getNickname()).isEqualTo("새 닉네임");
	}

	@Test
	void deletesCurrentUser() {
		User user = persistedUser();
		given(userRepository.findById(1L)).willReturn(Optional.of(user));

		userCommandService.delete(1L);

		verify(userRepository).delete(user);
	}

	@Test
	void rejectsUpdateForMissingUser() {
		given(userRepository.findById(99L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userCommandService.update(99L, new UpdateUserRequest("닉네임")))
			.isInstanceOf(BusinessException.class)
			.extracting(exception -> ((BusinessException) exception).getErrorCode())
			.isEqualTo(UserErrorCode.USER_NOT_FOUND);
	}

	private User persistedUser() {
		User user = User.create("player@example.com", "encoded-password", "플레이어");
		ReflectionTestUtils.setField(user, "id", 1L);
		return user;
	}
}

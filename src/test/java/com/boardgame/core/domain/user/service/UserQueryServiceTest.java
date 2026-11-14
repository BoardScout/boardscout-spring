package com.boardgame.core.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

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
class UserQueryServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserQueryService userQueryService;

	@Test
	void getsCurrentUserBySessionUserId() {
		User user = persistedUser();
		given(userRepository.findById(1L)).willReturn(Optional.of(user));

		var response = userQueryService.getById(1L);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.email()).isEqualTo("player@example.com");
		assertThat(response.nickname()).isEqualTo("플레이어");
	}

	@Test
	void throwsNotFoundWhenUserDoesNotExist() {
		given(userRepository.findById(99L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userQueryService.getById(99L))
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

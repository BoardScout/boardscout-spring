package com.boardgame.core.domain.user.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class UserTest {

	@Test
	void createInitializesUserWithDefaultRole() {
		User user = User.create("user@example.com", "hashed-password", "player");

		assertThat(user.getEmail()).isEqualTo("user@example.com");
		assertThat(user.getPasswordHash()).isEqualTo("hashed-password");
		assertThat(user.getNickname()).isEqualTo("player");
		assertThat(user.getRole()).isEqualTo(UserRole.USER);
		assertThat(user.getId()).isNull();
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "\t", "\n"})
	void createRejectsBlankEmail(String email) {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> User.create(email, "hashed-password", "player"))
			.withMessage("email must contain 1 to 255 characters");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "\t", "\n"})
	void createRejectsBlankPasswordHash(String passwordHash) {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> User.create("user@example.com", passwordHash, "player"))
			.withMessage("passwordHash must contain 1 to 255 characters");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "\t", "\n"})
	void createRejectsBlankNickname(String nickname) {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> User.create("user@example.com", "hashed-password", nickname))
			.withMessage("nickname must contain 1 to 50 characters");
	}

	@Test
	void createRejectsValuesLongerThanMappedColumns() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> User.create("a".repeat(256), "hashed-password", "player"));
		assertThatIllegalArgumentException()
			.isThrownBy(() -> User.create("user@example.com", "a".repeat(256), "player"));
		assertThatIllegalArgumentException()
			.isThrownBy(() -> User.create("user@example.com", "hashed-password", "a".repeat(51)));
	}

	@Test
	void updatesNickname() {
		User user = User.create("user@example.com", "hashed-password", "before");

		user.updateNickname("after");

		assertThat(user.getNickname()).isEqualTo("after");
	}
}

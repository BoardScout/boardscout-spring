package com.boardgame.core.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserRole role;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private User(String email, String passwordHash, String nickname) {
		this.email = requireText(email, "email", 255);
		this.passwordHash = requireText(passwordHash, "passwordHash", 255);
		this.nickname = requireText(nickname, "nickname", 50);
		this.role = UserRole.USER;
	}

	public static User create(String email, String passwordHash, String nickname) {
		return new User(email, passwordHash, nickname);
	}

	private static String requireText(String value, String fieldName, int maxLength) {
		if (value == null || value.isBlank() || value.length() > maxLength) {
			throw new IllegalArgumentException(
				fieldName + " must contain 1 to " + maxLength + " characters");
		}
		return value;
	}
}

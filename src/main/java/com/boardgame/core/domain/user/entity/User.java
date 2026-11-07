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

	// 정적 팩토리에서 전달받은 가입 정보를 검증하고 일반 사용자로 초기화합니다.
	private User(String email, String passwordHash, String nickname) {
		this.email = requireText(email, "email", 255);
		this.passwordHash = requireText(passwordHash, "passwordHash", 255);
		this.nickname = requireText(nickname, "nickname", 50);
		this.role = UserRole.USER;
	}

	// 암호화된 비밀번호를 사용하는 일반 사용자 엔티티를 생성합니다.
	public static User create(String email, String passwordHash, String nickname) {
		return new User(email, passwordHash, nickname);
	}

	// 필수 문자열이 비어 있거나 데이터베이스 컬럼 길이를 넘는지 확인합니다.
	private static String requireText(String value, String fieldName, int maxLength) {
		if (value == null || value.isBlank() || value.length() > maxLength) {
			throw new IllegalArgumentException(
				fieldName + " must contain 1 to " + maxLength + " characters");
		}
		return value;
	}
}

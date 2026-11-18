package com.boardgame.core.domain.user.repository;

import com.boardgame.core.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	// 대소문자 구분 없이 같은 이메일로 가입한 회원이 있는지 확인합니다.
	boolean existsByEmailIgnoreCase(String email);

	// 대소문자 구분 없이 이메일로 회원을 조회합니다.
	Optional<User> findByEmailIgnoreCase(String email);
}

package com.boardgame.core.domain.boardgame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.boardgame.entity.UserFavoriteGame;
import com.boardgame.core.domain.user.entity.User;

public interface UserFavoriteGameRepository extends JpaRepository<UserFavoriteGame, Long> {

	// 사용자가 해당 게임을 관심 등록했는지 조회합니다.
	Optional<UserFavoriteGame> findByUserAndBoardGame(User user, BoardGame boardGame);

	// 사용자의 관심 게임 전체를 최근 등록순으로 조회합니다. 응답에 게임 정보가 필요하므로 게임을 함께 읽어 N+1 조회를 막습니다.
	@EntityGraph(attributePaths = "boardGame")
	List<UserFavoriteGame> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}

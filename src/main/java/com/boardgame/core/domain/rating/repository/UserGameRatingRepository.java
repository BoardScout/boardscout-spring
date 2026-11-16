package com.boardgame.core.domain.rating.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.rating.entity.UserGameRating;
import com.boardgame.core.domain.user.entity.User;

public interface UserGameRatingRepository extends JpaRepository<UserGameRating, Long> {

	Optional<UserGameRating> findByUserAndBoardGame(User user, BoardGame boardGame);

	List<UserGameRating> findAllByUserIdOrderByUpdatedAtDesc(Long userId);
}

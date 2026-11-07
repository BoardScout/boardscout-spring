package com.boardgame.core.domain.boardgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardgame.core.domain.boardgame.entity.BoardGame;

public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {
}

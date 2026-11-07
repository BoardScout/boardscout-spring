package com.boardgame.core.domain.boardgame.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_games")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardGame {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}

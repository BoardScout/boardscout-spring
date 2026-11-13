package com.boardgame.core.domain.boardgame.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.boardgame.core.domain.boardgame.entity.BoardGame;

public record BoardGameSearchResult(
	List<BoardGameSearchItem> items,
	int page,
	int size,
	long totalElements,
	int totalPages
) {

	public static BoardGameSearchResult from(Page<BoardGame> result) {
		return new BoardGameSearchResult(
			result.getContent().stream().map(BoardGameSearchItem::from).toList(),
			result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
	}
}

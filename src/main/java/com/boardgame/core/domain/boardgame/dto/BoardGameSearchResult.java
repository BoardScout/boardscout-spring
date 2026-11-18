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

	// 페이지 조회 결과를 게임 목록과 페이지 정보를 담은 응답으로 변환합니다.
	public static BoardGameSearchResult from(Page<BoardGame> result) {
		return new BoardGameSearchResult(
			result.getContent().stream().map(BoardGameSearchItem::from).toList(),
			result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
	}
}

package com.boardgame.core.domain.boardgame.dto;

import java.util.List;

public record BoardGameCursorResult(
	List<BoardGameSearchItem> items,
	Long nextCursor,
	boolean hasNext
) {
}

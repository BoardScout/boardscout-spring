package com.boardgame.core.domain.boardgame.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boardgame.core.domain.boardgame.dto.BoardGameSearchResult;
import com.boardgame.core.domain.boardgame.dto.BoardGameCursorResult;
import com.boardgame.core.domain.boardgame.service.BoardGameCommandService;
import com.boardgame.core.domain.boardgame.service.BoardGameQueryService;
import com.boardgame.core.global.exception.BusinessException;
import com.boardgame.core.global.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/core/board-games")
@RequiredArgsConstructor
public class BoardGameController {

	private final BoardGameCommandService boardGameCommandService;
	private final BoardGameQueryService boardGameQueryService;

	@GetMapping
	public BoardGameSearchResult search(
		@RequestParam(defaultValue = "") String query,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	) {
		if (page < 0 || size < 1 || size > 50 || query.length() > 100) {
			throw new BusinessException(CommonErrorCode.BAD_REQUEST);
		}
		return boardGameQueryService.search(query, page, size);
	}

	@GetMapping("/cursor")
	public BoardGameCursorResult searchAfter(
		@RequestParam(defaultValue = "") String query,
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "24") int size
	) {
		if ((cursor != null && cursor < 0) || size < 1 || size > 50 || query.length() > 100) {
			throw new BusinessException(CommonErrorCode.BAD_REQUEST);
		}
		return boardGameQueryService.searchAfter(query, cursor, size);
	}
}

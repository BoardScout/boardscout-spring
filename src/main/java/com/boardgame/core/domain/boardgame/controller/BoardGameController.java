package com.boardgame.core.domain.boardgame.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boardgame.core.domain.boardgame.dto.BoardGameSearchResult;
import com.boardgame.core.domain.boardgame.dto.BoardGameCursorResult;
import com.boardgame.core.domain.boardgame.service.BoardGameQueryService;
import com.boardgame.core.global.exception.BusinessException;
import com.boardgame.core.global.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/board-games")
@RequiredArgsConstructor
public class BoardGameController {

	private final BoardGameQueryService boardGameQueryService;

	// 이름으로 보드게임을 검색해 페이지 단위로 반환합니다. 페이지 번호, 크기(최대 50), 검색어 길이(최대 100자)를 먼저 확인합니다.
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

	// 커서 방식으로 다음 보드게임 목록을 반환합니다. 커서와 크기, 검색어 길이가 허용 범위를 벗어나면 400 오류로 처리합니다.
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

package com.boardgame.core.domain.boardgame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.boardgame.core.domain.boardgame.dto.BoardGameSearchResult;
import com.boardgame.core.domain.boardgame.dto.BoardGameCursorResult;
import com.boardgame.core.domain.boardgame.dto.BoardGameSearchItem;
import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.boardgame.repository.BoardGameRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardGameQueryService {

	private final BoardGameRepository boardGameRepository;

	// 검색어가 없으면 전체, 있으면 이름이 일치하는 보드게임을 이름순 페이지로 조회합니다.
	public BoardGameSearchResult search(String query, int page, int size) {
		String keyword = query == null ? "" : query.strip();
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		Page<BoardGame> result = keyword.isEmpty()
			? boardGameRepository.findAll(pageable)
			: boardGameRepository.findByNameContainingIgnoreCaseOrNameKoContainingIgnoreCase(
				keyword, keyword, pageable);
		return BoardGameSearchResult.from(result);
	}

	// 마지막으로 받은 id 이후의 게임을 조회합니다. size보다 한 건 더 읽어 다음 페이지가 있는지 판단합니다.
	public BoardGameCursorResult searchAfter(String query, Long cursor, int size) {
		String keyword = query == null ? "" : query.strip();
		long normalizedCursor = cursor == null ? 0L : cursor;
		var games = boardGameRepository.findNextByCursor(
			normalizedCursor, keyword, PageRequest.of(0, size + 1));
		boolean hasNext = games.size() > size;
		var items = games.stream()
			.limit(size)
			.map(BoardGameSearchItem::from)
			.toList();
		Long nextCursor = hasNext && !items.isEmpty() ? items.getLast().id() : null;
		return new BoardGameCursorResult(items, nextCursor, hasNext);
	}
}

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

	public BoardGameSearchResult search(String query, int page, int size) {
		String keyword = query == null ? "" : query.strip();
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		Page<BoardGame> result = keyword.isEmpty()
			? boardGameRepository.findAll(pageable)
			: boardGameRepository.findByNameContainingIgnoreCaseOrNameKoContainingIgnoreCase(
				keyword, keyword, pageable);
		return BoardGameSearchResult.from(result);
	}

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

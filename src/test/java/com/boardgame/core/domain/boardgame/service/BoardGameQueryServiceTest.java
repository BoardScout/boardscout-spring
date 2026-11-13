package com.boardgame.core.domain.boardgame.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.boardgame.core.domain.boardgame.dto.BoardGameSearchResult;
import com.boardgame.core.domain.boardgame.dto.BoardGameCursorResult;
import com.boardgame.core.domain.boardgame.entity.BoardGame;
import com.boardgame.core.domain.boardgame.repository.BoardGameRepository;

@ExtendWith(MockitoExtension.class)
class BoardGameQueryServiceTest {

	@Mock
	private BoardGameRepository boardGameRepository;

	@Test
	void searchesEnglishAndKoreanNamesWithTheSameKeyword() {
		BoardGame game = BoardGame.fromLocalizedDataset(0, 30549L, "Pandemic", "팬데믹", null,
			null, null, "Medical", 2, 4, 45, 45, 8, 2008,
			new BigDecimal("2.4063"), new BigDecimal("7.58896"), Set.of(), Set.of());
		given(boardGameRepository.findByNameContainingIgnoreCaseOrNameKoContainingIgnoreCase(
			any(), any(), any(Pageable.class))).willReturn(new PageImpl<>(List.of(game)));

		BoardGameSearchResult result = new BoardGameQueryService(boardGameRepository).search(" 팬데믹 ", 0, 20);

		assertEquals("Pandemic", result.items().getFirst().nameEn());
		assertEquals("팬데믹", result.items().getFirst().nameKo());
		verify(boardGameRepository).findByNameContainingIgnoreCaseOrNameKoContainingIgnoreCase(
			eq("팬데믹"), eq("팬데믹"), any(Pageable.class));
	}

	@Test
	void returnsTheNextCursorWithoutOffsetPagination() {
		BoardGame first = BoardGame.fromLocalizedDataset(0, 30549L, "Pandemic", "팬데믹", null,
			null, null, "Medical", 2, 4, 45, 45, 8, 2008,
			new BigDecimal("2.4063"), new BigDecimal("7.58896"), Set.of(), Set.of());
		BoardGame second = BoardGame.fromLocalizedDataset(1, 13L, "Catan", "카탄", null,
			null, null, "Strategy", 3, 4, 60, 90, 10, 1995,
			new BigDecimal("2.3000"), new BigDecimal("7.10000"), Set.of(), Set.of());
		BoardGame overflow = BoardGame.fromLocalizedDataset(2, 822L, "Carcassonne", "카르카손", null,
			null, null, "Strategy", 2, 5, 30, 45, 7, 2000,
			new BigDecimal("1.9000"), new BigDecimal("7.40000"), Set.of(), Set.of());
		setId(first, 11L);
		setId(second, 12L);
		setId(overflow, 13L);
		given(boardGameRepository.findNextByCursor(eq(10L), eq(""), any(Pageable.class)))
			.willReturn(List.of(first, second, overflow));

		BoardGameCursorResult result = new BoardGameQueryService(boardGameRepository).searchAfter("", 10L, 2);

		assertEquals(2, result.items().size());
		assertEquals(12L, result.nextCursor());
		assertTrue(result.hasNext());
	}

	@Test
	void omitsCursorWhenTheLastSliceIsReached() {
		given(boardGameRepository.findNextByCursor(eq(0L), eq("카탄"), any(Pageable.class)))
			.willReturn(List.of());

		BoardGameCursorResult result = new BoardGameQueryService(boardGameRepository).searchAfter(" 카탄 ", null, 24);

		assertFalse(result.hasNext());
		assertEquals(null, result.nextCursor());
	}

	private static void setId(BoardGame game, long id) {
		try {
			var field = BoardGame.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(game, id);
		} catch (ReflectiveOperationException exception) {
			throw new AssertionError(exception);
		}
	}
}

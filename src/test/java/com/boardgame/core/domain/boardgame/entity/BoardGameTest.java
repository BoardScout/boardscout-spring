package com.boardgame.core.domain.boardgame.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class BoardGameTest {

	@Test
	void manuallyRegisteredGameHasNoDatasetIdentifiers() {
		BoardGame game = BoardGame.create(null, "Chess", "Classic game", "Strategy", 2, 2, 10, 120,
			6, 1475, new BigDecimal("3.0000"), new BigDecimal("8.50000"), Set.of("Abstract"), Set.of());

		assertNull(game.getId());
		assertNull(game.getDatasetItemId());
		assertNull(game.getBggId());
		assertEquals("Chess", game.getName());
		assertEquals("Classic game", game.getDescription());
		assertEquals("Strategy", game.getCategory());
		assertEquals(2, game.getMinPlayers());
		assertEquals(2, game.getMaxPlayers());
		assertEquals(10, game.getMinPlayTimeMinutes());
		assertEquals(120, game.getMaxPlayTimeMinutes());
		assertEquals(6, game.getMinAge());
		assertEquals(1475, game.getYearPublished());
		assertEquals(new BigDecimal("3.0000"), game.getDifficulty());
		assertEquals(new BigDecimal("8.50000"), game.getAverageRating());
	}

	@Test
	void datasetGamePreservesSeparateMlAndBggIdentifiersAndUnknownValues() {
		BoardGame game = dataset(0, 171L, 0, 0, 0, 0, 0, -3000,
			BigDecimal.ONE, BigDecimal.ZERO);

		assertNull(game.getId());
		assertNull(game.getCreatedBy());
		assertEquals(0, game.getDatasetItemId());
		assertEquals(171L, game.getBggId());
		assertEquals(-3000, game.getYearPublished());
	}

	@Test
	void datasetGameAllowsUnknownMaximumPlayerCount() {
		BoardGame game = dataset(1038, 1540L, 2, 0, 60, 60, 14, 1985,
			new BigDecimal("4.0000"), new BigDecimal("6.68000"));

		assertEquals(2, game.getMinPlayers());
		assertEquals(0, game.getMaxPlayers());
	}

	@Test
	void omittedCollectionsDefaultToEmpty() {
		BoardGame game = BoardGame.create(null, "Game", null, "Strategy", 1, 4, 0, 60,
			null, null, null, null, null, null);

		assertTrue(game.getCategories().isEmpty());
		assertTrue(game.getMechanics().isEmpty());
	}

	@Test
	void collectionsAreCopiedAndCannotBeMutatedThroughGetters() {
		Set<String> categories = new LinkedHashSet<>(List.of("Strategy", "Adventure"));
		Set<String> mechanics = new LinkedHashSet<>(List.of("Dice Rolling", "Deck Building"));
		BoardGame game = BoardGame.create(null, "Game", null, "Strategy", 1, 4, 0, 60,
			null, null, null, null, categories, mechanics);
		categories.clear();
		mechanics.clear();

		assertNotSame(categories, game.getCategories());
		assertIterableEquals(List.of("Strategy", "Adventure"), game.getCategories());
		assertIterableEquals(List.of("Dice Rolling", "Deck Building"), game.getMechanics());
		assertThrows(UnsupportedOperationException.class, () -> game.getCategories().clear());
		assertThrows(UnsupportedOperationException.class, () -> game.getMechanics().clear());
	}

	@Test
	void rejectsInvalidIdentifiersAndRanges() {
		assertThrows(IllegalArgumentException.class, () -> dataset(null, 1L, 1, 4, 0, 60, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> dataset(0, null, 1, 4, 0, 60, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> dataset(-1, 1L, 1, 4, 0, 60, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> dataset(0, 0L, 1, 4, 0, 60, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> dataset(0, 1L, -1, 4, 0, 60, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> dataset(0, 1L, 4, 1, 0, 60, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> dataset(0, 1L, 1, 4, -1, 60, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> dataset(0, 1L, 1, 4, 60, 10, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> dataset(0, 1L, 1, 4, 0, 60, -1, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> dataset(0, 1L, 1, 4, 0, 60, null, -3001, null, null));
		for (String difficulty : List.of("0.9999", "5.0001")) {
			assertThrows(IllegalArgumentException.class,
				() -> dataset(0, 1L, 1, 4, 0, 60, null, null, new BigDecimal(difficulty), null));
		}
		for (String rating : List.of("-0.00001", "10.00001")) {
			assertThrows(IllegalArgumentException.class,
				() -> dataset(0, 1L, 1, 4, 0, 60, null, null, null, new BigDecimal(rating)));
		}
	}

	@Test
	void rejectsBlankAndOversizedNamesAndCategories() {
		for (String name : new String[] {null, " ", "a".repeat(101)}) {
			assertThrows(IllegalArgumentException.class, () -> BoardGame.create(null, name, null, "Strategy",
				1, 4, 0, 60, null, null, null, null, null, null));
		}
		for (String category : new String[] {null, " ", "a".repeat(51)}) {
			assertThrows(IllegalArgumentException.class, () -> BoardGame.create(null, "Game", null, category,
				1, 4, 0, 60, null, null, null, null, null, null));
		}
	}

	private BoardGame dataset(Integer datasetItemId, Long bggId, int minPlayers, int maxPlayers,
		int minTime, int maxTime, Integer minAge, Integer year, BigDecimal difficulty, BigDecimal rating) {
		return BoardGame.fromDataset(datasetItemId, bggId, "Game", null, "Strategy", minPlayers,
			maxPlayers, minTime, maxTime, minAge, year, difficulty, rating, Set.of(), Set.of());
	}
}

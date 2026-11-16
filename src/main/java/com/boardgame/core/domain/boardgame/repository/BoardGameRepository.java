package com.boardgame.core.domain.boardgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.boardgame.core.domain.boardgame.entity.BoardGame;

public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {

	Optional<BoardGame> findByDatasetItemId(Integer datasetItemId);

	Page<BoardGame> findByNameContainingIgnoreCaseOrNameKoContainingIgnoreCase(
		String nameEn, String nameKo, Pageable pageable);

	@Query("""
		select game from BoardGame game
		where game.id > :cursor
		and (
			:keyword = ''
			or lower(game.name) like lower(concat('%', :keyword, '%'))
			or lower(coalesce(game.nameKo, '')) like lower(concat('%', :keyword, '%'))
		)
		order by game.id asc
		""")
	List<BoardGame> findNextByCursor(
		@Param("cursor") long cursor,
		@Param("keyword") String keyword,
		Pageable pageable);
}

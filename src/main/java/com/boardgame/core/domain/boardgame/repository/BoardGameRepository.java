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

	// 추천 데이터셋의 게임 ID로 보드게임 한 건을 조회합니다.
	Optional<BoardGame> findByDatasetItemId(Integer datasetItemId);

	// 영문 이름이나 한글 이름에 검색어가 포함된 보드게임을 대소문자 구분 없이 페이지 단위로 조회합니다.
	Page<BoardGame> findByNameContainingIgnoreCaseOrNameKoContainingIgnoreCase(
		String nameEn, String nameKo, Pageable pageable);

	// 커서(마지막으로 받은 id)보다 큰 게임을 id 순으로 조회합니다. 검색어가 비어 있으면 전체를 대상으로 합니다.
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

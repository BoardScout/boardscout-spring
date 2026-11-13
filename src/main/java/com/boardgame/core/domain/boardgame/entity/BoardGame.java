package com.boardgame.core.domain.boardgame.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.boardgame.core.domain.user.entity.User;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_games")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardGame {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	private User createdBy;

	@Column(name = "dataset_item_id", unique = true)
	private Integer datasetItemId;

	@Column(name = "bgg_id", unique = true)
	private Long bggId;

	@Column(name = "name_en", nullable = false, length = 100)
	private String name;

	@Column(name = "name_ko", length = 100)
	private String nameKo;

	@Column(name = "description_ko", columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_url", length = 2048)
	private String imageUrl;

	@Column(name = "thumbnail_url", length = 2048)
	private String thumbnailUrl;

	@Column(nullable = false, length = 50)
	private String category;

	@Column(name = "min_players", nullable = false)
	private int minPlayers;

	@Column(name = "max_players", nullable = false)
	private int maxPlayers;

	@Column(name = "min_play_time_minutes", nullable = false)
	private int minPlayTimeMinutes;

	@Column(name = "max_play_time_minutes", nullable = false)
	private int maxPlayTimeMinutes;

	@Column(name = "min_age")
	private Integer minAge;

	@Column(name = "year_published")
	private Integer yearPublished;

	@Column(precision = 5, scale = 4)
	private BigDecimal difficulty;

	@Column(precision = 7, scale = 5)
	private BigDecimal averageRating;

	@ElementCollection
	@CollectionTable(name = "board_game_categories", joinColumns = @JoinColumn(name = "board_game_id"))
	@Column(name = "category", nullable = false, length = 100)
	private Set<String> categories = new LinkedHashSet<>();

	@ElementCollection
	@CollectionTable(name = "board_game_mechanics", joinColumns = @JoinColumn(name = "board_game_id"))
	@Column(name = "mechanic", nullable = false, length = 100)
	private Set<String> mechanics = new LinkedHashSet<>();

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// 직접 등록과 데이터셋 등록이 공유하는 값 검증과 필드 초기화를 수행합니다.
	private BoardGame(User createdBy, Integer datasetItemId, Long bggId, String name, String description,
		String category, int minPlayers, int maxPlayers, int minPlayTimeMinutes, int maxPlayTimeMinutes,
		Integer minAge, Integer yearPublished, BigDecimal difficulty, BigDecimal averageRating,
		Set<String> categories, Set<String> mechanics, String nameKo, String imageUrl, String thumbnailUrl) {
		this.name = requireText(name, "name", 100);
		this.nameKo = optionalText(nameKo, "nameKo", 100);
		this.category = requireText(category, "category", 50);
		if (datasetItemId != null && datasetItemId < 0) {
			throw new IllegalArgumentException("datasetItemId must be nonnegative");
		}
		if (bggId != null && bggId <= 0) {
			throw new IllegalArgumentException("bggId must be positive");
		}
		if (minPlayers < 0 || maxPlayers < 0 || (maxPlayers != 0 && maxPlayers < minPlayers)) {
			throw new IllegalArgumentException(
				"player counts must be nonnegative and ordered unless maxPlayers is unknown");
		}
		if (minPlayTimeMinutes < 0 || maxPlayTimeMinutes < minPlayTimeMinutes) {
			throw new IllegalArgumentException("play times must be nonnegative and ordered");
		}
		if (minAge != null && minAge < 0) {
			throw new IllegalArgumentException("minAge must be nonnegative");
		}
		if (yearPublished != null && yearPublished < -3000) {
			throw new IllegalArgumentException("yearPublished must be at least -3000");
		}
		requireRange(difficulty, "difficulty", 1, 5);
		requireRange(averageRating, "averageRating", 0, 10);
		this.createdBy = createdBy;
		this.datasetItemId = datasetItemId;
		this.bggId = bggId;
		this.description = description;
		this.imageUrl = optionalText(imageUrl, "imageUrl", 2048);
		this.thumbnailUrl = optionalText(thumbnailUrl, "thumbnailUrl", 2048);
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.minPlayTimeMinutes = minPlayTimeMinutes;
		this.maxPlayTimeMinutes = maxPlayTimeMinutes;
		this.minAge = minAge;
		this.yearPublished = yearPublished;
		this.difficulty = difficulty;
		this.averageRating = averageRating;
		this.categories = copyLabels(categories, "categories");
		this.mechanics = copyLabels(mechanics, "mechanics");
	}

	// 사용자가 직접 등록한 보드게임을 데이터셋 식별자 없이 생성합니다.
	public static BoardGame create(User createdBy, String name, String description, String category,
		int minPlayers, int maxPlayers, int minPlayTimeMinutes, int maxPlayTimeMinutes, Integer minAge,
		Integer yearPublished, BigDecimal difficulty, BigDecimal averageRating, Set<String> categories,
		Set<String> mechanics) {
		return new BoardGame(createdBy, null, null, name, description, category, minPlayers, maxPlayers,
			minPlayTimeMinutes, maxPlayTimeMinutes, minAge, yearPublished, difficulty, averageRating,
			categories, mechanics, null, null, null);
	}

	// 추천 데이터셋의 내부 ID와 BGG ID를 보존한 보드게임을 생성합니다.
	public static BoardGame fromDataset(Integer datasetItemId, Long bggId, String name, String description,
		String category, int minPlayers, int maxPlayers, int minPlayTimeMinutes, int maxPlayTimeMinutes,
		Integer minAge, Integer yearPublished, BigDecimal difficulty, BigDecimal averageRating,
		Set<String> categories, Set<String> mechanics) {
		if (datasetItemId == null || bggId == null) {
			throw new IllegalArgumentException("datasetItemId and bggId are required for dataset games");
		}
		return new BoardGame(null, datasetItemId, bggId, name, description, category, minPlayers, maxPlayers,
			minPlayTimeMinutes, maxPlayTimeMinutes, minAge, yearPublished, difficulty, averageRating,
			categories, mechanics, null, null, null);
	}

	// API나 검수된 데이터에서 확보한 한국어 이름과 이미지 주소를 함께 보존합니다.
	public static BoardGame fromLocalizedDataset(Integer datasetItemId, Long bggId, String name,
		String nameKo, String description, String imageUrl, String thumbnailUrl, String category,
		int minPlayers, int maxPlayers, int minPlayTimeMinutes, int maxPlayTimeMinutes, Integer minAge,
		Integer yearPublished, BigDecimal difficulty, BigDecimal averageRating, Set<String> categories,
		Set<String> mechanics) {
		if (datasetItemId == null || bggId == null) {
			throw new IllegalArgumentException("datasetItemId and bggId are required for dataset games");
		}
		return new BoardGame(null, datasetItemId, bggId, name, description, category, minPlayers, maxPlayers,
			minPlayTimeMinutes, maxPlayTimeMinutes, minAge, yearPublished, difficulty, averageRating,
			categories, mechanics, nameKo, imageUrl, thumbnailUrl);
	}

	// 영속 컬렉션이 외부에서 직접 변경되지 않도록 읽기 전용 뷰를 반환합니다.
	public Set<String> getCategories() {
		return Collections.unmodifiableSet(categories);
	}

	// 영속 컬렉션이 외부에서 직접 변경되지 않도록 읽기 전용 뷰를 반환합니다.
	public Set<String> getMechanics() {
		return Collections.unmodifiableSet(mechanics);
	}

	// 필수 문자열의 공백 여부와 데이터베이스 컬럼 길이를 함께 검증합니다.
	private static String requireText(String value, String field, int maxLength) {
		if (value == null || value.isBlank() || value.length() > maxLength) {
			throw new IllegalArgumentException(field + " must contain 1 to " + maxLength + " characters");
		}
		return value;
	}

	private static String optionalText(String value, String field, int maxLength) {
		if (value == null) {
			return null;
		}
		return requireText(value, field, maxLength);
	}

	// 선택 숫자 값이 지정된 도메인 범위 안에 있는지 확인합니다.
	private static void requireRange(BigDecimal value, String field, int min, int max) {
		if (value != null && (value.compareTo(BigDecimal.valueOf(min)) < 0
			|| value.compareTo(BigDecimal.valueOf(max)) > 0)) {
			throw new IllegalArgumentException(field + " must be between " + min + " and " + max);
		}
	}

	// 입력 라벨을 검증해 엔티티가 소유하는 새 컬렉션으로 복사합니다.
	private static Set<String> copyLabels(Set<String> labels, String field) {
		Set<String> copy = new LinkedHashSet<>();
		if (labels != null) {
			for (String label : labels) {
				copy.add(requireText(label, field, 100));
			}
		}
		return copy;
	}
}

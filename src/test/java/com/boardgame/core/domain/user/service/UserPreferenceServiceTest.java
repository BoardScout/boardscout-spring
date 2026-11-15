package com.boardgame.core.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import com.boardgame.core.domain.user.dto.UserPreferenceRequest;
import com.boardgame.core.domain.user.dto.UserPreferenceResponse;
import com.boardgame.core.domain.user.entity.PreferencePlayStyle;
import com.boardgame.core.domain.user.entity.PreferenceTheme;
import com.boardgame.core.domain.user.entity.RecommendationMode;
import com.boardgame.core.domain.user.entity.User;
import com.boardgame.core.domain.user.entity.UserPreference;
import com.boardgame.core.domain.user.repository.UserPreferenceRepository;
import com.boardgame.core.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserPreferenceRepository userPreferenceRepository;

	private UserPreferenceService userPreferenceService;

	@BeforeEach
	void setUp() {
		userPreferenceService = new UserPreferenceService(userRepository, userPreferenceRepository);
	}

	@Test
	void createsPreferencesWhenUserHasNone() {
		User user = User.create("player@example.com", "encoded-password", "플레이어");
		UserPreferenceRequest request = new UserPreferenceRequest(
			RecommendationMode.CONTEXT, 4, 60, 2,
			List.of(PreferenceTheme.STRATEGY), List.of(PreferencePlayStyle.COOP), List.of());
		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(userPreferenceRepository.findById(1L)).willReturn(Optional.empty());
		given(userPreferenceRepository.save(any(UserPreference.class)))
			.willAnswer(invocation -> invocation.getArgument(0));

		UserPreferenceResponse response = userPreferenceService.save(1L, request);

		assertThat(response.recommendationMode()).isEqualTo(RecommendationMode.CONTEXT);
		assertThat(response.themes()).containsExactly(PreferenceTheme.STRATEGY);
		verify(userPreferenceRepository).save(any(UserPreference.class));
	}

	@Test
	void replacesExistingPreferences() {
		User user = User.create("player@example.com", "encoded-password", "플레이어");
		UserPreference preference = UserPreference.create(user);
		preference.update(
			RecommendationMode.CONTEXT, 4, 60, 2,
			List.of(PreferenceTheme.STRATEGY), List.of(PreferencePlayStyle.COOP), List.of());
		UserPreferenceRequest request = new UserPreferenceRequest(
			RecommendationMode.TASTE, null, null, null, List.of(), List.of(), List.of(0L, 1L, 15L));
		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(userPreferenceRepository.findById(1L)).willReturn(Optional.of(preference));
		given(userPreferenceRepository.save(preference)).willReturn(preference);

		UserPreferenceResponse response = userPreferenceService.save(1L, request);

		assertThat(response.recommendationMode()).isEqualTo(RecommendationMode.TASTE);
		assertThat(response.likedGameIds()).containsExactly(0L, 1L, 15L);
		assertThat(response.themes()).isEmpty();
	}
}

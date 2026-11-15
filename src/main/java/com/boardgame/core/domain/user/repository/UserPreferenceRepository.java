package com.boardgame.core.domain.user.repository;

import com.boardgame.core.domain.user.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
}

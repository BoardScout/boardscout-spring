package com.boardgame.core.domain.auth.dto;

import com.boardgame.core.domain.user.dto.UserResponse;

public record LoginResult(UserResponse user, String sessionId) {
}

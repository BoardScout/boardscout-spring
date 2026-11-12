package com.boardgame.core.domain.auth.security;

public record SessionPrincipal(Long userId, String email, String nickname, String role) {
}

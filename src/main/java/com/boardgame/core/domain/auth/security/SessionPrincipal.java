package com.boardgame.core.domain.auth.security;

/** Redis 세션에서 복원해 현재 요청의 인증 주체로 사용하는 최소 사용자 정보입니다. */
public record SessionPrincipal(String sessionId, Long userId, String email, String nickname, String role) {
}

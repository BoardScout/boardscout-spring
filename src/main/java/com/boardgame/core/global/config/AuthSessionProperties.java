package com.boardgame.core.global.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application-*.yml의 app.session 값을 인증 구성 요소가 사용할 타입으로 묶습니다.
 * 쿠키 이름과 TTL은 공통 설정을, Secure 여부는 활성 프로필의 값을 사용합니다.
 */
@ConfigurationProperties(prefix = "app.session")
public record AuthSessionProperties(String cookieName, Duration ttl, boolean secureCookie) {
}

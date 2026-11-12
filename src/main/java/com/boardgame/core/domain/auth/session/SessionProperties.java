package com.boardgame.core.domain.auth.session;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.session")
public record SessionProperties(String cookieName, Duration ttl, boolean secureCookie) {
}

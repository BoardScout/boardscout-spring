package com.boardgame.core.global.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.session")
public record AuthSessionProperties(String cookieName, Duration ttl, boolean secureCookie) {
}

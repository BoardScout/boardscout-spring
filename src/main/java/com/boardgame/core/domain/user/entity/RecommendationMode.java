package com.boardgame.core.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RecommendationMode {
	CONTEXT("context"),
	TASTE("taste");

	private final String value;

	RecommendationMode(String value) {
		this.value = value;
	}

	@JsonValue
	public String value() {
		return value;
	}
}

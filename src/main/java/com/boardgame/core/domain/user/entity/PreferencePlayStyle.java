package com.boardgame.core.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PreferencePlayStyle {
	COOP("coop"),
	SOCIAL("social"),
	ENGINE("engine"),
	LUCK("luck"),
	PUZZLE("puzzle"),
	SOLO("solo");

	private final String value;

	PreferencePlayStyle(String value) {
		this.value = value;
	}

	@JsonValue
	public String value() {
		return value;
	}
}

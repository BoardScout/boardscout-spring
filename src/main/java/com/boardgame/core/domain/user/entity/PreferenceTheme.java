package com.boardgame.core.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PreferenceTheme {
	STRATEGY("strategy"),
	PARTY("party"),
	ADVENTURE("adventure"),
	MYSTERY("mystery"),
	SCIFI("scifi"),
	FAMILY("family"),
	HISTORY("history"),
	BUILDING("building");

	private final String value;

	PreferenceTheme(String value) {
		this.value = value;
	}

	@JsonValue
	public String value() {
		return value;
	}
}

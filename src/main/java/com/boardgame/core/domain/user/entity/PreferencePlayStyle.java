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

	// 상수마다 API에서 사용할 문자열 값을 보관합니다.
	PreferencePlayStyle(String value) {
		this.value = value;
	}

	// JSON으로 주고받을 때 사용하는 소문자 값을 반환합니다.
	@JsonValue
	public String value() {
		return value;
	}
}

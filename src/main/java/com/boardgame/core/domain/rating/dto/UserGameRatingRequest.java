package com.boardgame.core.domain.rating.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record UserGameRatingRequest(
	@NotNull
	@DecimalMin("1.0")
	@DecimalMax("10.0")
	@Digits(integer = 2, fraction = 2)
	BigDecimal rating
) {
}

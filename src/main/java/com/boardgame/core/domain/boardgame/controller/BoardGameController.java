package com.boardgame.core.domain.boardgame.controller;

import org.springframework.web.bind.annotation.RestController;

import com.boardgame.core.domain.boardgame.service.BoardGameCommandService;
import com.boardgame.core.domain.boardgame.service.BoardGameQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BoardGameController {

	private final BoardGameCommandService boardGameCommandService;
	private final BoardGameQueryService boardGameQueryService;
}

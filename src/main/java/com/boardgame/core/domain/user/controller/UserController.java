package com.boardgame.core.domain.user.controller;

import com.boardgame.core.domain.user.service.UserCommandService;
import com.boardgame.core.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserCommandService userCommandService;
	private final UserQueryService userQueryService;
}

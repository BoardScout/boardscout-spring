package com.boardgame.core.domain.boardgame.service;

import org.springframework.stereotype.Service;

import com.boardgame.core.domain.boardgame.repository.BoardGameRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardGameQueryService {

	private final BoardGameRepository boardGameRepository;
}

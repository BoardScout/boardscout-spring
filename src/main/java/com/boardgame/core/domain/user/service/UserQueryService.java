package com.boardgame.core.domain.user.service;

import com.boardgame.core.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

	private final UserRepository userRepository;
}

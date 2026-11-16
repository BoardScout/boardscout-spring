CREATE TABLE user_game_ratings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    board_game_id BIGINT NOT NULL,
    rating DECIMAL(4, 2) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_user_game_ratings PRIMARY KEY (id),
    CONSTRAINT uk_user_game_ratings_user_game UNIQUE (user_id, board_game_id),
    CONSTRAINT fk_user_game_ratings_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_game_ratings_board_game
        FOREIGN KEY (board_game_id) REFERENCES board_games (id) ON DELETE CASCADE,
    CONSTRAINT chk_user_game_ratings_rating CHECK (rating BETWEEN 1.0 AND 10.0)
);

CREATE INDEX idx_user_game_ratings_user_updated
    ON user_game_ratings (user_id, updated_at DESC);

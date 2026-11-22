-- 관심 등록한 게임입니다. 같은 게임은 한 번만 등록되고, 회원이나 게임이 삭제되면 함께 지워집니다.
CREATE TABLE user_favorite_games (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    board_game_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_user_favorite_games PRIMARY KEY (id),
    CONSTRAINT uk_user_favorite_games_user_game UNIQUE (user_id, board_game_id),
    CONSTRAINT fk_user_favorite_games_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_favorite_games_board_game
        FOREIGN KEY (board_game_id) REFERENCES board_games (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_favorite_games_user_created
    ON user_favorite_games (user_id, created_at DESC);

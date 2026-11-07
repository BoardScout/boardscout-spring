CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE board_games (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by BIGINT NULL,
    dataset_item_id INT NULL,
    bgg_id BIGINT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    category VARCHAR(50) NOT NULL,
    min_players INT NOT NULL,
    max_players INT NOT NULL,
    min_play_time_minutes INT NOT NULL,
    max_play_time_minutes INT NOT NULL,
    min_age INT NULL,
    year_published INT NULL,
    difficulty DECIMAL(5, 4) NULL,
    average_rating DECIMAL(7, 5) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_board_games PRIMARY KEY (id),
    CONSTRAINT uk_board_games_dataset_item_id UNIQUE (dataset_item_id),
    CONSTRAINT uk_board_games_bgg_id UNIQUE (bgg_id),
    CONSTRAINT fk_board_games_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT chk_board_games_players CHECK (
        min_players >= 0 AND max_players >= 0 AND (max_players = 0 OR max_players >= min_players)
    ),
    CONSTRAINT chk_board_games_play_time CHECK (
        min_play_time_minutes >= 0 AND max_play_time_minutes >= min_play_time_minutes
    ),
    CONSTRAINT chk_board_games_difficulty CHECK (difficulty IS NULL OR difficulty BETWEEN 1.0 AND 5.0),
    CONSTRAINT chk_board_games_average_rating CHECK (average_rating IS NULL OR average_rating BETWEEN 0.0 AND 10.0)
);

CREATE INDEX idx_board_games_created_by ON board_games (created_by);

CREATE TABLE board_game_categories (
    board_game_id BIGINT NOT NULL,
    category VARCHAR(100) NOT NULL,
    CONSTRAINT pk_board_game_categories PRIMARY KEY (board_game_id, category),
    CONSTRAINT fk_board_game_categories_board_game
        FOREIGN KEY (board_game_id) REFERENCES board_games (id) ON DELETE CASCADE
);

CREATE TABLE board_game_mechanics (
    board_game_id BIGINT NOT NULL,
    mechanic VARCHAR(100) NOT NULL,
    CONSTRAINT pk_board_game_mechanics PRIMARY KEY (board_game_id, mechanic),
    CONSTRAINT fk_board_game_mechanics_board_game
        FOREIGN KEY (board_game_id) REFERENCES board_games (id) ON DELETE CASCADE
);

CREATE TABLE user_preferences (
    user_id BIGINT NOT NULL,
    recommendation_mode VARCHAR(20) NOT NULL,
    player_count INT NULL,
    max_play_time INT NULL,
    difficulty INT NULL,
    CONSTRAINT pk_user_preferences PRIMARY KEY (user_id),
    CONSTRAINT fk_user_preferences_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_user_preferences_mode CHECK (recommendation_mode IN ('CONTEXT', 'TASTE')),
    CONSTRAINT chk_user_preferences_player_count CHECK (player_count IS NULL OR player_count BETWEEN 1 AND 100),
    CONSTRAINT chk_user_preferences_max_play_time CHECK (max_play_time IS NULL OR max_play_time BETWEEN 10 AND 360),
    CONSTRAINT chk_user_preferences_difficulty CHECK (difficulty IS NULL OR difficulty BETWEEN 1 AND 3)
);

CREATE TABLE user_preference_themes (
    user_id BIGINT NOT NULL,
    sort_order INT NOT NULL,
    theme VARCHAR(20) NOT NULL,
    CONSTRAINT pk_user_preference_themes PRIMARY KEY (user_id, sort_order),
    CONSTRAINT fk_user_preference_themes_user FOREIGN KEY (user_id) REFERENCES user_preferences (user_id) ON DELETE CASCADE
);

CREATE TABLE user_preference_play_styles (
    user_id BIGINT NOT NULL,
    sort_order INT NOT NULL,
    play_style VARCHAR(20) NOT NULL,
    CONSTRAINT pk_user_preference_play_styles PRIMARY KEY (user_id, sort_order),
    CONSTRAINT fk_user_preference_play_styles_user FOREIGN KEY (user_id) REFERENCES user_preferences (user_id) ON DELETE CASCADE
);

CREATE TABLE user_preference_liked_games (
    user_id BIGINT NOT NULL,
    sort_order INT NOT NULL,
    game_id BIGINT NOT NULL,
    CONSTRAINT pk_user_preference_liked_games PRIMARY KEY (user_id, sort_order),
    CONSTRAINT uk_user_preference_liked_games UNIQUE (user_id, game_id),
    CONSTRAINT fk_user_preference_liked_games_user FOREIGN KEY (user_id) REFERENCES user_preferences (user_id) ON DELETE CASCADE,
    CONSTRAINT chk_user_preference_liked_game_id CHECK (game_id >= 0)
);

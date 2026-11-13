ALTER TABLE board_games
    CHANGE COLUMN name name_en VARCHAR(100) NOT NULL,
    CHANGE COLUMN description description_ko TEXT NULL,
    ADD COLUMN name_ko VARCHAR(100) NULL AFTER name_en,
    ADD COLUMN image_url VARCHAR(2048) NULL AFTER description_ko,
    ADD COLUMN thumbnail_url VARCHAR(2048) NULL AFTER image_url;

CREATE INDEX idx_board_games_name_en ON board_games (name_en);
CREATE INDEX idx_board_games_name_ko ON board_games (name_ko);

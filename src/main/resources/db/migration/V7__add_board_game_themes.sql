-- 전체 게임 카드에 보여 줄 테마 코드를 미리 계산해 둡니다.
-- 분류 기준은 fastapi-server/app/recommender.py 의 THEME_KEYWORDS 와 같습니다. 쉼표로 이어 저장하고, 해당 테마가 없으면 NULL입니다.
ALTER TABLE board_games ADD COLUMN themes VARCHAR(100) NULL AFTER category;

UPDATE board_games g SET g.themes = NULLIF(CONCAT_WS(',',
    IF(EXISTS (SELECT 1 FROM board_game_categories c WHERE c.board_game_id = g.id AND (LOWER(c.category) LIKE '%economic%' OR LOWER(c.category) LIKE '%civilization%' OR LOWER(c.category) LIKE '%strategy%' OR LOWER(c.category) LIKE '%political%' OR LOWER(c.category) LIKE '%wargame%')), 'strategy', NULL),
    IF(EXISTS (SELECT 1 FROM board_game_categories c WHERE c.board_game_id = g.id AND (LOWER(c.category) LIKE '%party game%' OR LOWER(c.category) LIKE '%humor%' OR LOWER(c.category) LIKE '%word game%' OR LOWER(c.category) LIKE '%bluffing%')), 'party', NULL),
    IF(EXISTS (SELECT 1 FROM board_game_categories c WHERE c.board_game_id = g.id AND (LOWER(c.category) LIKE '%adventure%' OR LOWER(c.category) LIKE '%fantasy%' OR LOWER(c.category) LIKE '%exploration%' OR LOWER(c.category) LIKE '%fighting%' OR LOWER(c.category) LIKE '%mythology%')), 'adventure', NULL),
    IF(EXISTS (SELECT 1 FROM board_game_categories c WHERE c.board_game_id = g.id AND (LOWER(c.category) LIKE '%deduction%' OR LOWER(c.category) LIKE '%murder%' OR LOWER(c.category) LIKE '%horror%' OR LOWER(c.category) LIKE '%mystery%' OR LOWER(c.category) LIKE '%spies%')), 'mystery', NULL),
    IF(EXISTS (SELECT 1 FROM board_game_categories c WHERE c.board_game_id = g.id AND (LOWER(c.category) LIKE '%science fiction%' OR LOWER(c.category) LIKE '%space exploration%' OR LOWER(c.category) LIKE '%futuristic%')), 'scifi', NULL),
    IF(EXISTS (SELECT 1 FROM board_game_categories c WHERE c.board_game_id = g.id AND (LOWER(c.category) LIKE '%children''s game%' OR LOWER(c.category) LIKE '%animals%' OR LOWER(c.category) LIKE '%family%' OR LOWER(c.category) LIKE '%educational%')), 'family', NULL),
    IF(EXISTS (SELECT 1 FROM board_game_categories c WHERE c.board_game_id = g.id AND (LOWER(c.category) LIKE '%medieval%' OR LOWER(c.category) LIKE '%ancient%' OR LOWER(c.category) LIKE '%world war%' OR LOWER(c.category) LIKE '%history%' OR LOWER(c.category) LIKE '%renaissance%')), 'history', NULL),
    IF(EXISTS (SELECT 1 FROM board_game_categories c WHERE c.board_game_id = g.id AND (LOWER(c.category) LIKE '%city building%' OR LOWER(c.category) LIKE '%territory building%' OR LOWER(c.category) LIKE '%industry%' OR LOWER(c.category) LIKE '%transportation%')), 'building', NULL)
), '');

# Flyway migrations

데이터베이스 스키마 변경 파일을 `V1__initial_schema.sql` 형식으로 추가합니다.

`V3__seed_board_game_catalog.sql`은 추천 서버의 `data/games.csv`에서 생성한 초기 카탈로그입니다.
새 MySQL에서 Spring이 시작되면 Flyway가 1,500개 게임과 카테고리·메커니즘을 자동 적재합니다.

원본 CSV를 바꾼 뒤 시드 SQL을 다시 만들려면 저장소 루트에서 다음 명령을 실행합니다.

```bash
fastapi-server/.venv/bin/python fastapi-server/scripts/export_mysql_catalog.py \
  --output spring-server/src/main/resources/db/migration/V3__seed_board_game_catalog.sql
```

설명은 인원·시간·난이도·카테고리·메커니즘만 사용해 생성하며, 실제 표지가 없는 게임에는
프런트의 8개 테마 플레이스홀더 중 하나를 지정합니다.


#!/bin/bash

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# 서버 디렉토리로 이동
cd "$PROJECT_ROOT/docker-compose/prod"

docker compose stop server_0
docker compose rm -f server_0
docker compose up -d --no-deps server_0

docker compose stop server_1
docker compose rm -f server_1
docker compose up -d --no-deps server_1

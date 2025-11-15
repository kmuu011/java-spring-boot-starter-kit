#!/bin/bash

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$PROJECT_ROOT"

"$PROJECT_ROOT/commands/prod-build-server-image.sh"

cd "$PROJECT_ROOT/docker-compose/prod"

docker compose up -d
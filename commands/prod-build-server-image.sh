#!/bin/bash

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$PROJECT_ROOT"

docker build -t production/spring-boot:latest -f ./docker-compose/java_spring_boot_starter_kit_prod/docker_file/spring/Dockerfile .
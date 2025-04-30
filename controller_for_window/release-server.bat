cd ..
cd docker-compose
cd java_spring_boot_starter_kit_prod

docker compose stop server_0
docker compose rm -f server_0
docker compose up -d --no-deps server_0

docker compose stop server_1
docker compose rm -f server_1
docker compose up -d --no-deps server_1

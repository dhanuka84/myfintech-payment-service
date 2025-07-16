.PHONY: build test docker-up docker-down liquibase-update

build:
	mvn clean package -DskipTests

test:
	mvn test

docker-up:
	docker-compose up --build -d

docker-down:
	docker-compose down

liquibase-update:
	mvn liquibase:update

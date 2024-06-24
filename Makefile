SHELL := /bin/bash
.PHONY: test install run help

API_CONTAINER="api"
DB_CONTAINER="database"
CACHE_CONTAINER="cache"

help: ## Show help message.
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m\033[0m\n"} /^[$$()% a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

test: ## Execute all test
	@mvn clean verify

run-api: ## Run API with maven
	@SPRING_PROFILES_ACTIVE=dev mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-javaagent:${PWD}/opentelemetry/opentelemetry-javaagent.jar -Dotel.javaagent.configuration-file=${PWD}/opentelemetry/dev.properties"

start-api: ## Run API with docker compose
	@docker compose up -d
	@docker compose logs -f ${API_CONTAINER}

kill-api: ## Kill API
	@docker compose rm -sf ${API_CONTAINER}

start-database: ## Run api database
	@docker compose up -d ${DB_CONTAINER}

kill-database: ## Kill api database
	@docker compose rm -sf ${DB_CONTAINER}
	@docker volume rm -f api_database

start-cache: ## Run api cache
	@docker compose up -d ${CACHE_CONTAINER}

kill-cache: ## Kill api database
	@docker compose rm -sf ${CACHE_CONTAINER}
	@docker volume rm -f api_cache

start-infra: ## Run required infrastructure with docker compose
	$(MAKE) kill start-database start-cache

restart-infra: ## Restart required infrastructure with docker compose
	$(MAKE) start-database start-cache

start-all: ## Run all containers with docker compose
	$(MAKE) start-infra start-cache start-api

restart-all: ## Restart containers with docker compose
	@docker compose stop ${API_CONTAINER} # used to rebuild API after modification
	@docker compose up -d
	@docker compose logs -f ${API_CONTAINER}

kill: ## Kill and reset project
	@docker compose down
	@mvn install -DskipTests
	$(MAKE) kill-database kill-cache kill-api

release: ## Create release
	./scripts/create_release.sh

hotfix: ## Create hotfix
	./scripts/create_hotfix.sh

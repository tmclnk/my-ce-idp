.PHONY: help clean start

help: ## Print Help
	@egrep -h '\s##\s' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

clean: ## Clean
	./mvnw clean

start : clean ## Start fake IdP app
	./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-ea"

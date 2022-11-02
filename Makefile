.PHONY: help discovery bearer-token

BASE_URL:=https://dmsi-poc.us.authz.cloudentity.io/dmsi-poc/legacy-integration
TOKEN_URL:=$(BASE_URL)/oauth2/token

help: ## Print Help
	@egrep -h '\s##\s' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

bearer-token: ## Get Bearer Token
	@curl --silent --request POST \
	  --url '$(TOKEN_URL)' \
	  --header 'content-type: application/x-www-form-urlencoded' \
	  --data grant_type=client_credentials \
	  --data client_id=$(CLOUDENTITY_CLIENT_ID) \
	  --data client_secret=$(CLOUDENTITY_CLIENT_SECRET) | jq .

login-screen: ## Open the CE Login Screen
	open $(BASE_URL)/app

clean: ## Clean
	./mvnw clean

start : clean ## Start fake IdP app
	./mvnw spring-boot:run
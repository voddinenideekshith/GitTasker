

# Decentralized Task Manager (Spring Boot, Thymeleaf, Redis, IPFS)

## Overview
This project is a Spring Boot web application for decentralized task management. It features:
- Task CRUD (create, read, update, delete) with full info (title, description, CID, status)
- IPFS integration for decentralized storage
- Redis caching for fast API responses
- Thymeleaf UI with Bootstrap for modern design
- Robust error handling and feedback
- Full unit, integration, and repository tests

## Endpoints

### Web UI
- `/tasks` — List all tasks
- `/tasks/new` — Create a new task
- `/tasks/edit/{id}` — Edit a task
- `/tasks/{id}/delete` — Delete a task

### REST API
- `GET /tasks/api` — List all tasks (JSON)
- `POST /tasks/api` — Create task (JSON)
- `GET /tasks/api/ipfs/{cid}` — Retrieve task by IPFS CID
- `PUT /tasks/api/{id}` — Update task
- `DELETE /tasks/api/{id}` — Delete task

## Features
- **Redis Caching:** Frequently accessed CIDs and task metadata are cached for performance.
- **Error Handling:** All backend and UI errors are caught and displayed as feedback messages.
- **Testing:** Unit, integration, and repository tests cover all layers. Run `mvn test` to execute.
- **Bootstrap UI:** Responsive, clean design with feedback for all operations.

## Setup & Usage
1. Install JDK 21 and Redis server (default: localhost:6379)
2. Clone this repo and open in VS Code
3. Build and run:
	 ```powershell
	 mvn clean install
	 mvn spring-boot:run
	 ```
4. Access the app at [http://localhost:8081/tasks](http://localhost:8081/tasks)

## Testing
- Run all tests:
	```powershell
	mvn test
	```
- Tests cover service, controller, and repository logic

### How to run tests

- Run the full test suite locally:
	```powershell
	mvn test
	```
- Notes:
	- Service and controller tests use Mockito (unit-style) to mock dependencies where appropriate.
	- Repository tests are either mocked with Mockito or use an embedded MongoDB (flapdoodle) for integration-style tests when enabled.

## Notes
- IPFS data is immutable; updates create new CIDs
- Redis caching is used for performance, not persistence
- Error messages are shown for all backend and decentralized failures


## Integration Challenges & Solutions

### IPFS
- **Challenge:** Connecting to a local IPFS node and handling failures gracefully.
- **Solution:** Used lazy initialization for the IPFS client in the service layer. If IPFS is down, the app shows clear error messages and continues to function for non-IPFS operations.

### Redis
- **Challenge:** Ensuring Redis caching improves performance without breaking persistence or causing downtime issues.
- **Solution:** Configured Spring Boot to use Redis for caching only (not as a primary store). All cache operations are wrapped in try-catch blocks, and errors are surfaced in the UI and API responses. The app falls back to direct DB queries if Redis is unavailable.

### OAuth2
- **Challenge:** Integrating OAuth2 login for user identification and handling missing or anonymous users.
- **Solution:** Used Spring Security’s OAuth2 client. If a user is not authenticated, the app defaults to an "anonymous" owner for tasks. All user-related operations are checked for null principal and handled gracefully.

## Screenshots & Demos
## Live Azure URL

Your app is deployed at:
```
https://internship1.centralindia.azurecontainerapps.io
```
(If this URL does not work, check the Azure Portal for the exact FQDN of your Container App.)

## Deployment Instructions

1. Install Azure CLI and Azure Developer CLI (`azd`).
2. Clone the repository.
3. Set your Azure region:
	```powershell
	azd env set AZURE_LOCATION centralindia
	```
4. Deploy to Azure:
	```powershell
	azd up
	```
5. After deployment, find your app URL in the terminal output or Azure Portal.

## Azure deployment (short description)

- This repository is prepared to run as a container in Azure Container Apps. The app image can be stored in an Azure Container Registry (ACR).
- The application expects the MongoDB connection string to be provided via the environment variable `SPRING_DATA_MONGODB_URI`. In Azure Container Apps the value should be supplied as a secret and referenced in the container template via `secretRef` (the secret name used here is `mongo-uri`).
- CI: A GitHub Actions workflow lives at `.github/workflows/maven-test.yml` and runs the test suite on pushes and pull requests to `main`. Additional CI steps (build/push/deploy) can be added to perform image builds and Container App updates; the repository also contains helper scripts in the `scripts/` folder to create an Azure service principal and prepare secrets for GitHub.

## CI/CD

- `maven-test.yml`: runs the test suite on every push and pull request to `main`.
- `deploy.yml`: builds the production artifact, builds and pushes a Docker image to ACR, and updates the Azure Container App when changes are pushed to `main` or when manually triggered.

Required GitHub secrets for `deploy.yml`:

- `AZURE_CREDENTIALS`: JSON output of `az ad sp create-for-rbac --sdk-auth` (used by `azure/login` action).
- `ACR_NAME`: your ACR registry name (example: `cruxojaw7gyw4hu`). The workflow uses `${ACR_NAME}.azurecr.io` as the registry host.
- `CONTAINERAPP_NAME`: the Container App name (example: `internship1`).
- `RESOURCE_GROUP`: the Azure resource group containing the Container App (example: `rg-dev`).

- `SPRING_DATA_MONGODB_URI` / `mongo-uri`: the MongoDB connection string used at runtime. For production deploys store the full Mongo URI as a GitHub secret (or store in Azure Key Vault and reference it in the Container App). The Container App expects the secret name `mongo-uri` and the app reads it via `SPRING_DATA_MONGODB_URI`.

### For report / mentor

- **Architecture:** Spring Boot web app (Thymeleaf UI) using Spring Data MongoDB for persistence, Redis for caching, and IPFS for decentralized storage. The app is packaged as a fat JAR and run inside a Docker container.
- **Tools & Services:** Java 21, Spring Boot 3.x, MongoDB (Atlas or managed), Embedded Mongo (flapdoodle) for tests, Azure Container Registry (ACR) for images, Azure Container Apps for runtime, GitHub Actions for CI/CD.
- **CI/CD Flow:**
	- `maven-test.yml` runs on every push and PR to `main` and executes `mvn test` (fast feedback loop).
	- `deploy.yml` runs on pushes to `main` (or manual dispatch). It builds the artifact, builds and pushes a Docker image to ACR tagged with `${{ github.sha }}`, and updates the Azure Container App to use that image (it also sets a `--revision-suffix` derived from the short Git SHA so revisions are easy to identify).
- **What I learned:** Automating CI/CD with GitHub Actions and Azure CLI simplifies deployments but requires careful secret management (service principal JSON) and understanding of registry policies (some ACRs disallow tasks/in-registry builds). Tests should be fast and deterministic — using Mockito for service/controller tests and embedded Mongo for optional integration tests keeps the suite reliable.
- **Future work:** Add health/readiness endpoints (Spring Boot Actuator), centralized logging and monitoring (Azure Monitor / Application Insights), nicer UI/UX for the Thymeleaf frontend, and more E2E tests running against a deployed staging environment. Consider using Azure Key Vault for secrets instead of storing secrets directly in GitHub when possible.

To create `AZURE_CREDENTIALS` locally and add it to GitHub secrets:

```powershell
az ad sp create-for-rbac --sdk-auth --name "github-actions-acr" > azure-sp.json
# Then add the contents of azure-sp.json to the GitHub repo secret named AZURE_CREDENTIALS
# and set ACR_NAME, CONTAINERAPP_NAME, RESOURCE_GROUP accordingly.
```

## Environment Variable Requirements

Set these in your Azure environment or `.env` file:
- `AZURE_LOCATION=centralindia`
- `AZURE_CONTAINER_REGISTRY_ENDPOINT`
- `AZURE_KEY_VAULT_ENDPOINT`
- `AZURE_KEY_VAULT_NAME`
- `AZURE_RESOURCE_INTERNSHIP1_ID`
- `AZURE_RESOURCE_REDIS_ID`
- `AZURE_RESOURCE_VAULT_ID`
- `AZURE_SUBSCRIPTION_ID`
- `SERVICE_INTERNSHIP1_IMAGE_NAME`

Database (Postgres) config in `application.postgres.yml`:
```yaml
spring:
  datasource:
	 url: jdbc:postgresql://<host>:5432/<db>
	 username: <user>
	 password: <password>
```

## Screenshots

_No screenshots available yet. Use Windows Snipping Tool or PrtScn to capture your app UI and add images here._


# DMH

Digital Money House (DMH) is designed to manage users, accounts, cards, and financial transactions in a
secure and scalable digital wallet system.

The system models financial operations through a structured Entity-Relationship design that reflects 
real-world constraints and banking rules.

## Software Used

- Java 25 jdk
- Maven 3.9.12
- Docker
- Keycloak
- PostgreSQL
- Spring Boot 4.0

## Microservices

- api-eureka (8761) - Service discovery
- api-gateway (8080) - Central router
- api-user (8081) - Users + Auth (Keycloak)
- api-account (8082) - Accounts, CVU, alias
- api-card (8083) - Cards
- api-transaction (8084) - Transfers

## Main Endpoints (api-user)

- POST /api/user/register - Register a new user
- POST /api/user/login - Authenticate and get tokens
- POST /api/user/logout - End session (header X-Refresh-Token)
- POST /api/user/{keycloakId}/send-verification - Send verification email
- POST /api/user/{keycloakId}/reset-password - Trigger password reset

## How to Run the Application

### 1. Clone the repository

```bash
git clone https://github.com/RODRIGONAHUELKIRSCH/DMH.git
cd DMH
```

### Prerequisites

Make sure you have the following installed:

- [Docker](https://www.docker.com/get-started) and Docker Compose
- [Java 25 JDK](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.9+](https://maven.apache.org/download.cgi) (only required for local development / running tests)
- [Git](https://git-scm.com/downloads)
- [Keycloak](https://www.keycloak.org/downloads) — used as the identity provider for `api-user`

### 1.1. Configure Keycloak (first time only)

The `api-user` microservice delegates authentication to Keycloak. The first time
you set up the environment you **must** configure Keycloak to persist its data
in a dedicated PostgreSQL database instead of the default embedded H2 database
(which can be lost or corrupted between restarts).

The configuration script is provided at
[`temp-guides/keycloack-config.txt`](temp-guides/keycloack-config.txt). It
exports the environment variables that point Keycloak to a PostgreSQL
database (`keycloak_db` on `localhost:5432`) and then starts Keycloak in
optimized mode.

Run it the first time (from the directory where your Keycloak `kc.bat` is
located, or adjust the path accordingly):

**Windows (cmd):**

```cmd
temp-guides\keycloack-config.txt
```

Or copy the contents of [`temp-guides/keycloack-config.txt`](temp-guides/keycloack-config.txt)
into your terminal to execute them line by line:

```cmd
set KC_DB=postgres
set KC_DB_URL=jdbc:postgresql://localhost:5432/keycloak_db
set KC_DB_USERNAME=keycloak_user
set KC_DB_PASSWORD=1234

kc.bat start --http-enabled=true --hostname-strict=false --optimized
```

> Make sure the `keycloak_db` database and the `keycloak_user` role already
> exist in your local PostgreSQL before running the script. This avoids relying
> on Keycloak's default in-memory database, which can be lost or corrupted
> between restarts.

After Keycloak is up, create the `dmh-realm` realm and the `api-user` client
(credentials must match the ones declared in `docker-compose.yml`).

### 2. Build and run with Docker Compose

The project ships a `docker-compose.yml` that orchestrates the full stack
(`postgres`, `api-eureka`, `api-user`, `api-gateway`).

```bash
docker compose up --build
```

This will:
- Build the Docker images for `api-eureka`, `api-user` and `api-gateway`.
- Start a PostgreSQL instance and wait until it is healthy.
- Start the Eureka service discovery (`http://localhost:8761`).
- Start the `api-user` microservice (`http://localhost:8081`).
- Start the `api-gateway` (`http://localhost:8083`).

To stop the stack:

```bash
docker compose down
```

To stop the stack and remove persisted data (volumes):

```bash
docker compose down -v
```

### 3. Verify the stack is up

Once the containers are running, you can check:

- Eureka dashboard: http://localhost:8761
- api-user health: http://localhost:8081/actuator/health
- api-gateway health: http://localhost:8083/actuator/health

---

## Testing

### Running the tests

The project has automated tests for the `api-user` microservice. The easiest way
to run **all** the tests is to execute the [`ApiUserControllerTest`](Project/DMH/api-user/src/test/java/com/dmh/UserController/ApiUserControllerTest.java:1) suite, which is the integration suite that triggers the full
test suite (service unit tests + controller integration tests + context tests).`

Test coverage included in this microservice:

- Unit tests in [`ApiUserServiceTest`](Project/DMH/api-user/src/test/java/com/dmh/UserService/ApiUserServiceTest.java:1)
- Integration tests in [`ApiUserControllerTest`](Project/DMH/api-user/src/test/java/com/dmh/UserController/ApiUserControllerTest.java:1)
- 1 context test in [`ApiUserApplicationTests`](Project/DMH/api-user/src/test/java/com/dmh/ApiUserApplicationTests.java:1)

### Test documentation

The general testing strategy is documented in [`TestDocs/api-user/01-plan-testing.md`](TestDocs/api-user/01-plan-testing.md).
This document is split into two separate technical files for implementation details:

- Testing with **JUnit + Mockito** → [`TestDocs/api-user/02-junit-mockito-tests.md`](TestDocs/api-user/02-junit-mockito-tests.md)
- Testing with **RestAssured** → [`TestDocs/api-user/03-restassured-tests.md`](TestDocs/api-user/03-restassured-tests.md)

### Manual testing

The exploratory / manual testing for the `api-user` microservice is documented in
[`TestDocs/api-user/ManualTestingApiUser.docx`](TestDocs/api-user/ManualTestingApiUser.docx).

### Postman collection

A Postman collection is provided to exercise the endpoints manually.

- **Download link:** https://research-specialist-54290331-s-team.postman.co/workspace/My-Workspace~1ab275e5-9ebd-493c-83b5-0f6c973eb246/collection/36146276-cd85997a-2c1a-4c85-80f5-98d80cf5d298&action=share&source=copy-link&creator=36146276
- **JSON file (offline):** [`PostmanCollection/DMH Wallet.postman_collection.json`](PostmanCollection/DMH%20Wallet.postman_collection.json)

You can import the JSON file directly into Postman via
*File → Import → Upload Files* and select the file from the `PostmanCollection/`
folder.

## Architecture
##  Entity - Relation Diagram
![Diagrams/MER-DMH.png](https://github.com/RODRIGONAHUELKIRSCH/DMH/blob/main/Diagrams/DER-DMH.png)

## Relational Diagram
![Diagrams/MR-DMH.png](https://github.com/RODRIGONAHUELKIRSCH/DMH/blob/main/Diagrams/DR-DMH.png)

## User
Represents a registered user of the platform.

**Attributes:**
- id (PK)
- name
- lastname
- dni
- email
- password
- verified_email

A user may optionally verify their email address.  
The User entity acts as the root entity of the system.

---

## Account
Represents a digital wallet account.

**Attributes:**
- id (PK)
- cvu
- amount
- alias

An account:
- Belongs to exactly one user.
- Can perform multiple transactions.
- Can have multiple associated cards.

The CVU and alias provide two alternative mechanisms for transferring funds.

---

## Card
Represents a debit/credit card associated with an account.

**Attributes:**
- id (PK)
- card_number
- card_type
- card_company
- card_due_date

A card:
- Belongs to exactly one account.
- Can perform transactions independently of the account entity.

---

## Transactions
Represents financial operations within the system.

**Attributes:**
- id (PK)
- date
- type
- description
- amount
- state

The `state` attribute models the lifecycle of a transaction, allowing the system to manage asynchronous approval flows and business validations.

---

## Business Rules

- A User can have multiple Accounts (1:N).
- An Account belongs to exactly one User.
- An Account can perform multiple Transactions.
- A Card belongs to exactly one Account.
- A Card can perform multiple Transactions.
- A Transaction must be executed by either an Account or a Card.

---

## Design Decisions

### Card

Card was modeled as a separate entity instead of being embedded within Account because:

- It has its own lifecycle (expiration date, provider, type).
- It can independently perform transactions.
- It avoids overloading the Account entity.
- It maintains normalization principles.

---

### Transactions

Transactions were designed as an independent entity because:

- They represent immutable financial history.
- They centralize financial operations.
- Both Account and Card can generate transactions.

---

## Cardinality Decisions

Cardinalities were defined according to real-world financial constraints:

- An account cannot exist without a user.
- A card cannot exist without an account.
- A transaction must always belong to a single executor entity.
- A user may exist without having accounts.

---

## Model Limitations

- Currency type is not explicitly modeled (assumed to be Argentine pesos).
- Transaction fees are not represented.
- No joint accounts are supported.

---
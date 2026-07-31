# AGENTS.md — Spring 6 Reactive

## Build & verification

```bash
# Full build (w/ docker image + helm packaging)
./mvnw clean install -DskipTests

# Fast verify (skip docker & springboot start/stop)
./mvnw clean verify -Dskip.start.stop.springboot=true -Dskip.docker.build=true -Dskip.docker.publish=true

# Single test class
./mvnw test -pl . -Dtest=BeerRepositoryTest

# Run only integration tests (suffix *IT)
./mvnw verify -Dit.test='*IT'

# Format check (validate phase): spring-javaformat + spotless
./mvnw validate
```

Build order: `validate` (format) → `compile` → `test` (surefire, *Test) → `verify` (failsafe, *IT) → `install` (docker image + helm package). CI runs `mvn -B -e deploy`.

## Test quirks

- Tests use `@ActiveProfiles("test")` — in-memory H2 via R2DBC, no auth-server needed.
- `@ActiveProfiles("it")` only for `AuthServerHealthIndicatorIT` (requires real auth-server on :9000).
- Classes ending `*Test` run before `*IT` (`TestClassOrderer`).
- Within a class: `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` + `@Order` — the suite relies on ordered execution and shared state (bootstrap data, `@DirtiesContext`).
- Controller ITs authenticate via `mockJwt().authorities(new SimpleGrantedAuthority(READ_SCOPE))`.
- Repository tests: `@DataR2dbcTest` + `@Import({DatabaseConfig.class, BootstrapData.class})` — use `StepVerifier` or `TransactionalOperator` for rollback.
- `LocaleExtension` (auto-discovered) sets `Locale.US` globally.

## Architecture

- **Stack**: Spring Boot 4.1.0, WebFlux, R2DBC (H2 in-mem), OAuth2 resource server, Java 25, virtual threads.
- **API**: `/api/v2/beer`, `/api/v2/customer` — reactive controllers returning `Mono<T>` / `Flux<T>`.
- **Data**: `schema.sql` → `DatabaseConfig` (`ConnectionFactoryInitializer`). Bootstrap inserts 3 beers + 4 customers at startup.
- **MapStruct**: `defaultComponentModel=spring` (pom.xml compiler arg). Mapper interfaces + Lombok.
- **Security**: OAuth2 scopes `message.read` (GET) / `message.write` (POST/PUT/PATCH/DELETE). Actuator endpoints bypass auth.

## Formatting

- `spring-javaformat-maven-plugin` validate (spaces, not tabs — `.springjavaformatconfig`).
- `spotless-maven-plugin check` — sorts pom.xml, formats markdown/json/yaml/sh.

## Docker & K8s

```bash
./mvnw clean package spring-boot:build-image   # Docker image
docker compose up                               # app :8082 + auth-server :9000
```

Helm packaging is part of `install` phase. Charts in `helm-charts/` (Maven-filtered `@property@` placeholders).

## Dependency management

Renovate + Dependabot both run:
- **Dependabot**: Maven deps (`pom.xml`) + GitHub Actions.
- **Renovate**: maven-wrapper, docker-compose, k8s manifests, helm charts, Java version (Liberica LTS via custom datasource). Manages `domboeckli/*` Docker images with Maven versioning (supports `-SNAPSHOT`). All updates PR-only, no automerge. Branch prefix `feature/renovate-`, only `master` branch.

## CI (GitHub Actions)

- `maven-build.yml`: build → SonarCloud → deploy trigger.
- `release.yml`: `mvn release:prepare release:perform` (main/master only, must be SNAPSHOT).
- CI profile `ci-cd` auto-activates via `env.GITHUB_ACTIONS=true`.

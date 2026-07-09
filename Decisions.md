# Technical Decisions — Notes API

This document records major engineering decisions inferred from the repository: source code, configuration files, Git history, and project structure. Where the history shows a choice changed over time, both the earlier and current decisions are noted.

**Evidence base:** commits `242af8d` (28 June 2026, original app) through `cc27893` (9 July 2026, merge and consolidation).

---

## 1. Spring Boot 4.1

| | |
| --- | --- |
| **Chosen** | Spring Boot `4.1.0` as the parent POM and application framework |
| **Why** | The project was bootstrapped with Spring Initializr conventions (`HELP.md` references Boot 4.1.0 documentation). Boot provides embedded Tomcat, auto-configuration, and starter dependencies for web, JPA, security, and validation in a single module. |
| **Alternatives** | Plain Spring Framework without Boot; alternative JVM frameworks such as Quarkus or Micronaut. No evidence in the repository that these were evaluated. |
| **Tradeoffs** | **Gain:** fast setup, large ecosystem, consistent dependency management via the BOM. **Cost:** framework coupling, heavier runtime than micro-framework alternatives, upgrade tied to Spring release cadence. |

---

## 2. Java 21

| | |
| --- | --- |
| **Chosen** | Java 21 (`<java.version>21</java.version>` in the current `pom.xml`) |
| **Why** | The July 2026 rewrite (`4347f08`) set Java 21 explicitly. The Dockerfile uses `maven:3.9-eclipse-temurin-21-alpine` (build) and `eclipse-temurin:21-jre-alpine` (runtime). |
| **Earlier state** | The original app commit (`242af8d`) declared Java **26**, which caused class-file version mismatches when running tests on a Java 21 runtime. |
| **Alternatives** | Java 17 (current LTS at the time of many Boot 3.x projects). No commit message documents an explicit evaluation. |
| **Tradeoffs** | **Gain:** aligns build, Docker image, and local JDK; avoids preview/early-access JDK issues seen with Java 26. **Cost:** requires JDK 21 on developer machines. |

---

## 3. Database — PostgreSQL (initial) → MySQL (current)

### 3a. PostgreSQL (June 2026, superseded)

| | |
| --- | --- |
| **Chosen** | PostgreSQL as the sole runtime database |
| **Why** | The first application commit (`242af8d`) configured `jdbc:postgresql://localhost:5432/notes_db` and included only the PostgreSQL driver. The June README documented PostgreSQL as the tech stack. |
| **Alternatives** | None recorded in Git at this stage. |
| **Tradeoffs** | **Gain:** strong SQL support, common in production tutorials. **Cost:** required a running PostgreSQL instance for local development. |

### 3b. MySQL (July 2026, current default)

| | |
| --- | --- |
| **Chosen** | MySQL 8.4 as the default production database (`application.properties`, `docker-compose.yml`) |
| **Why** | The July rewrite (`4347f08`) switched `application.properties` to MySQL with environment-variable overrides and added `docker-compose.yml` with a `mysql:8.4` service, health check, and persistent volume. |
| **Alternatives** | PostgreSQL (previously used in this same repository). H2 was not chosen for production — only for dev/test profiles. |
| **Tradeoffs** | **Gain:** Docker Compose provides a self-contained local/prototype deployment; MySQL is widely available. **Cost:** dialect-specific behavior; migration from the earlier PostgreSQL setup required config changes. |

**Note:** The PostgreSQL driver remains declared in `pom.xml` but no `application*.properties` profile uses it. This appears to be a leftover dependency, not an active dual-database design.

---

## 4. H2 (development and testing)

| | |
| --- | --- |
| **Chosen** | H2 in-memory database for the `test` profile (`application-test.properties`, `ddl-auto=create-drop`) and the `h2` profile (`application-h2.properties`, `ddl-auto=update`) |
| **Why** | Added in the July rewrite to run integration tests without external infrastructure (`NotesApiIntegrationTest` uses `@ActiveProfiles("test")`). The `h2` profile (`39ec22c`) enables local runs without MySQL. |
| **Alternatives** | Testcontainers with MySQL (not present in `pom.xml`). Using MySQL for all environments (rejected for tests — slower, requires external service). |
| **Tradeoffs** | **Gain:** fast, isolated tests; zero-setup local development with `-Dspring-boot.run.profiles=h2`. **Cost:** H2 SQL semantics differ from MySQL; `MODE=PostgreSQL` is set in H2 URLs to approximate compatibility. Not representative of production MySQL behavior. |

---

## 5. Spring Data JPA / Hibernate

| | |
| --- | --- |
| **Chosen** | Spring Data JPA with Hibernate as the ORM; repository interfaces (`JpaRepository`) for `User` and `Note` |
| **Why** | Present from the first application commit. JPA annotations (`@Entity`, `@ManyToOne`, `@PrePersist`) manage the schema and relationships. Custom JPQL in `NoteRepository.searchNotesByUser()` handles keyword search. |
| **Alternatives** | JDBC template, jOOQ, MyBatis — none appear in the dependency tree. |
| **Tradeoffs** | **Gain:** rapid CRUD, pagination via `Pageable`, relationship mapping. **Cost:** abstraction leaks (N+1 risk with `LAZY` fetch), less control over SQL than hand-written queries. |

---

## 6. Hibernate `ddl-auto` (no migration tool)

| | |
| --- | --- |
| **Chosen** | `spring.jpa.hibernate.ddl-auto=update` for default and `h2` profiles; `create-drop` for the `test` profile |
| **Why** | Schema is generated and updated automatically from entities. No Flyway or Liquibase dependency exists in `pom.xml`. |
| **Alternatives** | Flyway or Liquibase for versioned migrations (listed as planned in README roadmap, not implemented). Manual SQL scripts. |
| **Tradeoffs** | **Gain:** zero migration boilerplate during development. **Cost:** not safe for production schema evolution; no auditable migration history; `update` may produce unexpected DDL changes. |

---

## 7. JWT authentication (stateless)

| | |
| --- | --- |
| **Chosen** | Stateless JWT via `jjwt` 0.12.6; `JwtFilter` extracts `Authorization: Bearer` tokens; `SessionCreationPolicy.STATELESS`; CSRF disabled |
| **Why** | Introduced in the July rewrite (`4347f08`) when per-user note scoping was added. The original app (`242af8d`) had no authentication — all `/api/notes` endpoints were public. JWT fits a REST API consumed by clients that store a token. |
| **Alternatives** | Server-side sessions with cookies (not chosen — sessions explicitly disabled). OAuth2 / OIDC (not implemented). API keys (not implemented). Refresh tokens (listed in roadmap, not implemented). |
| **Tradeoffs** | **Gain:** stateless scaling, no server session store, straightforward Bearer header usage. **Cost:** no built-in revocation; token theft exposes access until expiry; refresh flow not implemented. |

---

## 8. Custom `EmailPasswordAuthenticationProvider`

| | |
| --- | --- |
| **Chosen** | A custom `AuthenticationProvider` that authenticates by **email** and password, backed by `CustomUserDetailsService` and BCrypt |
| **Why** | `UserLoginRequestDTO` uses `email` (not username) as the login identifier. `CustomUserDetails.getUsername()` returns the user's email, which becomes the JWT subject. The default `DaoAuthenticationProvider` with username-based lookup was not used. |
| **Alternatives** | Spring's default `DaoAuthenticationProvider` with username login. Third-party auth (Auth0, Keycloak) — not present. |
| **Tradeoffs** | **Gain:** login identifier matches JWT subject and API contract. **Cost:** custom provider must be registered and maintained; Spring warns about `AuthenticationProvider` vs `UserDetailsService` bean ordering in startup logs. |

---

## 9. BCrypt password hashing

| | |
| --- | --- |
| **Chosen** | `BCryptPasswordEncoder` bean in `PasswordEncoderConfig`; passwords encoded in `UserService.register()` |
| **Why** | Spring Security's default recommendation for password storage. Plain-text passwords are never persisted. |
| **Alternatives** | Argon2, PBKDF2, SCrypt — supported by Spring Security but not configured. |
| **Tradeoffs** | **Gain:** well-understood, adaptive cost factor, integrated with Spring Security. **Cost:** slower than SHA (by design); BCrypt has a 72-byte input limit (not an issue for typical passwords). |

---

## 10. DTO pattern

| | |
| --- | --- |
| **Chosen** | Separate request/response DTOs for all HTTP boundaries; JPA entities (`User`, `Note`) are not returned from controllers |
| **Why** | The July rewrite introduced `dto/` package with seven DTO classes. The original app (`242af8d`) returned the `Notes` entity directly from `NotesController`, coupling the API to the persistence model. |
| **Alternatives** | Expose entities directly (previous approach in this repository). Java `record` types for immutable DTOs (not used — plain classes with getters/setters). |
| **Tradeoffs** | **Gain:** hides `password`, `user` FK, and lazy-loading concerns from API consumers; independent API versioning. **Cost:** mapping boilerplate; DTOs must be kept in sync with entities. |

---

## 11. Manual mapper components

| | |
| --- | --- |
| **Chosen** | Hand-written `@Component` mapper classes (`UserMapper`, `NoteMapper`) with explicit field assignment |
| **Why** | Both mappers exist as simple POJO-to-POJO converters. `lombok` is declared in `pom.xml` but **not used** in any `src/main` source file. MapStruct is not a dependency. |
| **Alternatives** | **MapStruct** (compile-time mapping, not adopted). **Lombok** (`@Builder`, `@Data` — declared but unused). **ModelMapper / MapStruct** — not in `pom.xml`. Inline mapping in services (partially used: `UserService.login()` and `NoteService.updateNote()` bypass the mapper). |
| **Tradeoffs** | **Gain:** no code-generation setup, easy to read, no reflection. **Cost:** verbose; mapping logic is inconsistent (some paths use mapper, others set fields directly). |

---

## 12. Layered architecture

| | |
| --- | --- |
| **Chosen** | Controller → Service → Repository, with DTOs, mappers, security, and exception handling as supporting packages |
| **Why** | Present from the first commit (`Controller` / `Service` / `Repository`). The July rewrite added `dto`, `mapper`, `security`, `config`, and lowercase package names under `com.Harshit.note_app`. |
| **Alternatives** | Hexagonal / clean architecture with ports and adapters (not adopted). Monolithic controller with direct repository access (rejected — not used after rewrite). |
| **Tradeoffs** | **Gain:** clear separation of concerns, testable services, standard Spring Boot layout. **Cost:** more classes and indirection for a small API; package rename during rewrite caused merge conflicts with duplicate beans. |

---

## 13. Bean Validation (`@Valid`)

| | |
| --- | --- |
| **Chosen** | Jakarta Bean Validation on request DTOs; `@Valid` on controller method parameters; `spring-boot-starter-validation` |
| **Why** | Used from the first commit (`@Valid @RequestBody Notes note`). Validation annotations (`@NotBlank`, `@Email`, `@Size`) are on DTO fields with custom message strings. |
| **Alternatives** | Manual validation in service layer (used only for business rules like duplicate username and invalid sort field, not for field format). JSON Schema validation — not used. |
| **Tradeoffs** | **Gain:** declarative, reusable constraints; fails fast before service logic. **Cost:** multiple field errors are joined into a single comma-separated message in `GlobalExceptionHandler`, losing per-field structure in the current `ApiErrorResponse` format. |

---

## 14. Unified `ApiErrorResponse` error format

| | |
| --- | --- |
| **Chosen** | `GlobalExceptionHandler` (`@RestControllerAdvice`) returns `ApiErrorResponse` for all handled errors; also implements `AuthenticationEntryPoint` and `AccessDeniedHandler` |
| **Why** | Introduced in the July rewrite. The original handler (`Exception/GlobalExceptionHandler.java`) returned `Map<String, String>` for validation errors only. The new handler covers 400, 401, 403, 404, and 409 with a consistent JSON shape. |
| **Alternatives** | Per-field error map (previous approach, visible in `validation.png` screenshots). RFC 7807 Problem Details (`application/problem+json`) — not used. Spring Boot default Whitelabel error page — overridden for API paths. |
| **Tradeoffs** | **Gain:** consistent client parsing, single handler for controller and filter-chain errors. **Cost:** validation errors lose field-level structure; unhandled exceptions (e.g. `IllegalStateException`) are not mapped. |

---

## 15. Per-user note scoping

| | |
| --- | --- |
| **Chosen** | `Note.user` (`@ManyToOne`); all note queries filter by the authenticated user; `findByIdAndUser()` for single-note access |
| **Why** | Added with JWT authentication in the July rewrite. The original app had no `User` entity — all notes were globally accessible. |
| **Alternatives** | Global notes with no ownership (previous behavior). Row-level security at the database level — not used. |
| **Tradeoffs** | **Gain:** multi-user safety enforced in service and repository layers. **Cost:** every query must include user filtering; returning 404 (not 403) for another user's note avoids leaking existence. |

---

## 16. SpringDoc OpenAPI (Swagger UI)

| | |
| --- | --- |
| **Chosen** | `springdoc-openapi-starter-webmvc-ui` 3.0.2; `OpenApiConfig` defines Bearer JWT scheme; controller methods annotated with `@Operation` / `@ApiResponses` |
| **Why** | Added in the July rewrite. Provides interactive API documentation at `/swagger-ui.html`, publicly accessible per `SecurityConfig`. |
| **Alternatives** | Springfox (deprecated for Boot 3+). Hand-written OpenAPI YAML only. No documentation tool (rejected for a portfolio API project). |
| **Tradeoffs** | **Gain:** auto-generated, interactive docs with JWT authorize button. **Cost:** dependency on third-party library compatibility with Spring Boot 4.x; annotations add noise to controllers. |

---

## 17. Maven build

| | |
| --- | --- |
| **Chosen** | Maven with `spring-boot-maven-plugin`; `mvnw` wrapper scripts included |
| **Why** | Standard Spring Initializr output from the first commit. `HELP.md` references Maven documentation. |
| **Alternatives** | Gradle (not present). |
| **Tradeoffs** | **Gain:** XML POM, BOM-managed versions, wide IDE support. **Cost:** `.mvn/` wrapper directory is missing from the repository, so `mvnw` cannot run without restoring wrapper files; system `mvn` is required locally. |

---

## 18. Docker multi-container deployment

| | |
| --- | --- |
| **Chosen** | `docker-compose.yml` with MySQL 8.4 and the Spring Boot app; multi-stage `Dockerfile` (`maven:3.9-eclipse-temurin-21-alpine` build → `eclipse-temurin:21-jre-alpine` runtime) |
| **Why** | Added in the July rewrite (`4347f08`). Compose wires JDBC credentials and JWT env vars; MySQL health check gates app startup. Build stage uses system Maven (not `mvnw`) because `.mvn/` is absent from the repository. |
| **Alternatives** | Single-container image with embedded H2 (not chosen for production-like setup). Kubernetes manifests — not present. Running JAR directly without containers (documented as the local dev path). |
| **Tradeoffs** | **Gain:** reproducible environment, persistent MySQL volume, health-checked startup order. **Cost:** image size and build time exceed a plain `java -jar` workflow; build stage downloads Maven dependencies on first build. |

---

## 19. Environment-variable configuration

| | |
| --- | --- |
| **Chosen** | Database and JWT settings externalized via environment variables with defaults in `application.properties`; `.env.example` for Docker Compose |
| **Why** | `application.properties` uses `${SPRING_DATASOURCE_URL:...}` placeholders. `docker-compose.yml` passes `SPRING_DATASOURCE_*`, `JWT_SECRET`, and `JWT_EXPIRATION` to the app container. |
| **Alternatives** | Hard-coded credentials only (original PostgreSQL config used literal `postgres` / `000`). Spring Cloud Config — not used. |
| **Tradeoffs** | **Gain:** 12-factor style config; different credentials per environment without code changes. **Cost:** dev defaults include a known JWT secret string — unsafe if deployed without override. |

---

## 20. Testing strategy

| | |
| --- | --- |
| **Chosen** | 25 tests: unit tests with Mockito (`UserServiceTest`, `NoteServiceTest`, `JwtServiceTest`), one integration test class with MockMvc (`NotesApiIntegrationTest`), minimal `NoteAppApplicationTests` |
| **Why** | Test suite added in the July rewrite (`4347f08`). Integration tests use H2 via the `test` profile. The original app had only a `@SpringBootTest` context-load test. |
| **Alternatives** | Full end-to-end tests against MySQL (not implemented). `@WebMvcTest` slice tests per controller — not used. |
| **Tradeoffs** | **Gain:** core auth and note lifecycle verified; fast H2-backed integration tests. **Cost:** gaps in HTTP-level coverage for pagination, search, sorting, and 409 responses; H2 may not catch MySQL-specific SQL issues. |

---

## Decisions not made (evidence of absence)

| Topic | Repository evidence |
| ----- | ------------------- |
| Refresh tokens | Listed in README roadmap; no implementation in `security/` |
| Role-based access control | `CustomUserDetails.getAuthorities()` returns empty list |
| Database migrations (Flyway/Liquibase) | No dependency or migration files |
| CORS configuration | No `CorsConfiguration` bean or `WebMvcConfigurer` |
| Spring Actuator | No `spring-boot-starter-actuator` dependency |
| Lombok usage | Declared in `pom.xml`, zero annotations in `src/main` |
| PostgreSQL (current) | Driver in `pom.xml`, no active configuration profile |
| CI/CD pipeline | No `.github/workflows`, Jenkinsfile, or similar |
| LICENSE | No `LICENSE` file in the repository |

---

## Decision timeline (Git history)

| Date | Commit | Decision change |
| ---- | ------ | ---------------- |
| 2026-06-28 | `242af8d` | Spring Boot 4.1, PostgreSQL, Java 26, entity-exposing API, no auth |
| 2026-06-28 | `a5ff95b` | API screenshots captured against unauthenticated API |
| 2026-07-09 | `4347f08` | JWT auth, MySQL, Java 21, DTOs, mappers, H2 tests, Docker, 25 tests |
| 2026-07-09 | `39ec22c` | H2 dev profile, POM BOM alignment |
| 2026-07-09 | `cc27893` | Merge; removed duplicate legacy packages from June history |

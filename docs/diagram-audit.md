# Architecture and Database Diagram Audit

Comparison of every architecture and database diagram (or diagram description) in this repository against the current implementation in `note-app/src/main/java/com/Harshit/note_app/`.

**Scope:** ASCII flows in `README.md`, the Docker Compose diagram in `README.md`, planned PNG specs in `docs/assets-plan.md`, and the package tree in `docs/project-tree.md`. API screenshots under `images/` are out of scope — they document HTTP examples, not system architecture.

**Last reviewed:** 2026-07-10 (against commit implementing JWT auth, MySQL default, `User` + `Note` entities only).

---

## Diagram inventory

| Location | Type | Subject |
| -------- | ---- | ------- |
| `README.md` § Project Structure | ASCII tree | Repository layout |
| `README.md` § Architecture Overview | ASCII flows | Servlet, registration, login, protected note, exception |
| `README.md` § Database Overview | Table | JPA entities and relationships |
| `README.md` § Docker Instructions | ASCII diagram | `app` → `mysql` |
| `docs/project-tree.md` | ASCII tree | Repository layout (expanded) |
| `docs/assets-plan.md` | PNG specs (not yet created) | Layered architecture, request flow, JWT login, registration, database schema, Docker Compose |
| `images/*.png` | Screenshots | API responses — **not** architecture diagrams |

No booking, refresh-token, RBAC, or PostgreSQL architecture diagrams exist. `docs/assets-plan.md` correctly lists those as not recommended.

---

## Implementation baseline

| Area | Current state |
| ---- | ------------- |
| JPA entities | `User`, `Note` only (`model/User.java`, `model/Note.java`) |
| Relationship | Unidirectional `@ManyToOne` on `Note.user` → `users.id` via `user_id` (NOT NULL). No `@OneToMany` on `User`. |
| Packages | Lowercase under `com.Harshit.note_app` (`controller`, `service`, `repository`, `model`, `dto`, `mapper`, `security`, `exception`, `config`) |
| Auth | Stateless JWT; login subject = email; registration does **not** issue a token |
| Endpoints | 7 total: `POST /api/auth/register`, `POST /api/auth/login`, 5 `/api/notes` routes |
| Booking | **Not implemented** — no booking entities, services, or endpoints |

---

## Findings by category

### Missing entities

| Finding | Severity | Detail |
| ------- | -------- | ------ |
| No missing entities in active diagrams | — | Implementation has exactly two JPA entities. Diagrams and tables correctly show only `User` and `Note`. |
| Historical `Notes` entity (June 2026) | Info | Pre-JWT rewrite exposed entities directly. No current diagram references this; `Decisions.md` documents the superseded design. |
| DTO / security types absent from ERD | Info | Correct omission — DTOs (`dto/`), `CustomUserDetails`, and JWT artifacts are not persisted. Planned `database-schema.png` should remain entity-only. |

### Missing relationships

| Finding | Severity | Detail |
| ------- | -------- | ------ |
| Unidirectional ownership not stated | Medium | `User` has no inverse `notes` collection. ERD descriptions should show `users` ← `notes.user_id` only, not a bidirectional association. |
| FK constraints not fully specified | Low | `user_id` is `nullable = false` on `@JoinColumn`. Deleting a user with existing notes is not handled in application code (no cascade rules). |
| `content` column type | Low | `Note.content` uses `@Column(columnDefinition = "TEXT")`. Schema descriptions should note TEXT, not generic VARCHAR. |
| Physical column names | Low | Spring Boot default naming maps `createdAt` → `created_at`, `updatedAt` → `updated_at`. Entity field names in docs are correct; ERD column labels should use snake_case for actual DB tables. |

### Outdated package names

| Finding | Severity | Detail |
| ------- | -------- | ------ |
| Capital-case legacy packages | Low | Empty directories `Controller/`, `Service/`, `Model/`, `Repository/`, `Exception/` exist on disk but contain no classes. Active diagrams use lowercase names — correct. `docs/project-tree.md` documents the empty dirs. |
| `entity/` package | — | Not used; entities live in `model/`. `docs/packages.md` already notes this. No diagram references `entity/`. |
| README project tree incomplete | Medium | `README.md` § Project Structure omits `Decisions.md` and `docs/` (present in `docs/project-tree.md`). |

### Outdated request flow

| Finding | Severity | Detail |
| ------- | -------- | ------ |
| `JwtFilter` scope | Medium | Filter runs on **every** request, including public `/api/auth/*` and Swagger paths. On those routes it no-ops when no Bearer header is present. Servlet flow should state this explicitly. |
| `/h2-console` in servlet flow | Low | Enabled by `h2` profile but not listed in the servlet flow authorization branch. It is protected (requires JWT) per `SecurityConfig.anyRequest().authenticated()`. |
| Registration validation path | Low | `@Valid` on `UserRegisterRequestDTO` can return `400` via `GlobalExceptionHandler` before `UserService` runs. Registration flow diagram omits this branch. |
| `createNote` ownership assignment | Medium | After `NoteMapper.toEntity()`, `NoteService.createNote()` calls `note.setUser(currentUser)` from `SecurityUtils`. Protected flow should include this step. |
| `DELETE` response | Low | `deleteNote()` returns void → `204 No Content`. Protected flow only mentions "void for DELETE 204" in the generic servlet flow, not the note-specific flow. |
| List/search/pagination | Info | `GET /api/notes` supports `keyword`, `page`, `size`, `sortBy` query params and branches to `searchNotesByUser` or `findByUser`. Simplified protected flow is acceptable but should be labeled as simplified. |
| Invalid JWT handling nuance | — | **Accurate:** invalid Bearer tokens clear `SecurityContext` and continue the chain; `401` comes from the authorization check, not from `JwtFilter` directly. |

### Outdated authentication flow

| Finding | Severity | Detail |
| ------- | -------- | ------ |
| Registration does not issue JWT | Medium | `UserService.register()` returns `UserResponseDTO` only. Clients must call `POST /api/auth/login` separately. Registration flow should state "no token issued". |
| Login identifier | — | **Accurate:** authentication uses **email** (`UserLoginRequestDTO.email`, JWT subject = email). Username is returned in the login response but is not the security principal. |
| `GlobalExceptionHandler` dual role | — | **Accurate:** implements `AuthenticationEntryPoint` and `AccessDeniedHandler` for filter-chain failures. |
| Refresh tokens / OAuth / sessions | — | Not implemented. No diagram should depict them. `docs/assets-plan.md` correctly excludes `refresh-token-flow.png` and `oauth-login.png`. |

### Outdated booking flow

| Finding | Severity | Detail |
| ------- | -------- | ------ |
| No booking feature | — | Repository has no booking entities, controllers, or endpoints. **No booking diagram exists or should be created.** `docs/assets-plan.md` § Not recommended correctly excludes `booking-flow.png`. |

---

## Per-diagram status

### `README.md` — Servlet request flow

| Check | Status |
| ----- | ------ |
| `JwtFilter` inside `SecurityFilterChain` | Accurate |
| Public paths | Accurate (`register`, `login`, Swagger/OpenAPI) |
| `401`/`403` via `GlobalExceptionHandler` | Accurate |
| `JwtFilter` on all requests | **Add note** |
| `/h2-console` protected | **Add to authorization branch** |

### `README.md` — Registration flow

| Check | Status |
| ----- | ------ |
| Duplicate → `409` | Accurate |
| BCrypt + mapper path | Accurate |
| `@Valid` → `400` before service | **Add branch** |
| No JWT on registration | **Add note** |

### `README.md` — Login flow

| Check | Status |
| ----- | ------ |
| `EmailPasswordAuthenticationProvider` chain | Accurate |
| JWT subject = email | Accurate |
| Bad credentials → `401` | Accurate |

### `README.md` — Protected note request flow

| Check | Status |
| ----- | ------ |
| `SecurityUtils` + `findByIdAndUser` ownership | Accurate |
| `createNote` → `note.setUser(currentUser)` | **Add step** |
| `DELETE` → `204` | **Add note** |
| List query params | Optional simplification — label as such |

### `README.md` — Database Overview (table)

| Check | Status |
| ----- | ------ |
| Two entities only | Accurate |
| `@ManyToOne` via `user_id` | Accurate |
| Unidirectional relationship | **Add note** |
| `content` as TEXT | **Add note** |
| Physical `created_at` columns | **Add note** |

### `README.md` — Docker Compose diagram

| Check | Status |
| ----- | ------ |
| Two-service layout | Accurate |
| Service names `app` / `mysql` | **Add labels** (containers: `notes-api`, `notes-mysql`) |
| `JWT_SECRET` / `JWT_EXPIRATION` env | **Add to description** |
| `depends_on: service_healthy` | Documented |
| Docker build | **Fixed:** `Dockerfile` uses `maven:3.9-eclipse-temurin-21-alpine` — no `mvnw` / `.mvn/` required |

### `docs/assets-plan.md` — Planned PNG specs

Specs are largely aligned with README flows. Updates applied in this audit:

- `database-schema.png`: unidirectional FK, TEXT `content`, snake_case column names, no `User.notes` collection
- `registration-flow.png`: `@Valid` → `400` branch; no JWT in response
- `request-flow-protected.png`: `note.setUser(currentUser)` on create; `204` on delete
- `layered-architecture.png`: `JwtFilter` nested under `SecurityFilterChain`, not a parallel top-level filter
- `jwt-login-flow.png`: clarify registration is a separate path with no token

---

## Recommendations (documentation only)

Do **not** redraw existing ASCII diagrams. Apply these text updates instead:

1. **`README.md` § Architecture Overview** — Add a short "Diagram accuracy" note linking here. Extend flow descriptions with the gaps listed above (JwtFilter scope, registration validation, `setUser` on create, no JWT on register).
2. **`README.md` § Database Overview** — Document unidirectional relationship, TEXT column, and Hibernate physical naming (`created_at`, `user_id`).
3. **`README.md` § Docker Instructions** — Name services/containers and env vars; note the Maven Wrapper build issue.
4. **`README.md` § Project Structure** — Align tree with `docs/project-tree.md` (`Decisions.md`, `docs/`).
5. **`docs/assets-plan.md`** — Keep PNG specs in sync with corrected README descriptions (done in this pass).
6. **`docs/project-tree.md`** — Include `docs/assets-plan.md` and this file in the tree.
7. **Do not create** `booking-flow.png`, refresh-token, OAuth, or PostgreSQL architecture assets.

When PNG diagrams are eventually generated from `docs/assets-plan.md`, use this audit as the acceptance checklist before embedding them in `README.md`.

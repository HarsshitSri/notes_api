# Notes API

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Neon](https://img.shields.io/badge/DB-Neon-00E599?logo=neon&logoColor=white)](https://neon.tech)
[![Railway](https://img.shields.io/badge/API-Railway-0B0D0E?logo=railway&logoColor=white)](https://railway.app)
[![Vercel](https://img.shields.io/badge/UI-Vercel-000000?logo=vercel&logoColor=white)](https://vercel.com)
[![Tests](https://img.shields.io/badge/Tests-25-blue)](#testing)
[![License](https://img.shields.io/badge/License-TBD-lightgrey)](#license)

A REST API for user registration, JWT authentication, and per-user note management — with a simple web UI. Built with Spring Boot, Spring Security, and PostgreSQL. Intended production layout: **Neon** (DB), **Railway** (API), **Vercel** (UI).

**Repository:** [github.com/HarsshitSri/notes_api](https://github.com/HarsshitSri/notes_api)

## Table of Contents

- [Documentation map](#documentation-map)
- [Project Overview](#project-overview)
- [Motivation](#motivation)
- [Features](#features)
- [Current Development Status](#current-development-status)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
  - [Layers and dependencies](#layers-and-dependencies)
  - [Servlet request flow](#servlet-request-flow)
  - [Registration flow](#registration-flow)
  - [Login flow](#login-flow)
  - [Protected note request flow](#protected-note-request-flow)
  - [Exception flow](#exception-flow)
- [Database Overview](#database-overview)
- [Authentication Overview](#authentication-overview)
  - [JWT reference](#jwt-reference)
- [API Overview](#api-overview)
  - [POST `/api/auth/register`](#post-apiauthregister)
  - [POST `/api/auth/login`](#post-apiauthlogin)
  - [POST `/api/notes`](#post-apinotes)
  - [GET `/api/notes`](#get-apinotes)
  - [GET `/api/notes/{id}`](#get-apinotesid)
  - [PUT `/api/notes/{id}`](#put-apinotesid)
  - [DELETE `/api/notes/{id}`](#delete-apinotesid)
  - [Endpoint summary](#endpoint-summary)
- [Web UI](#web-ui)
- [Getting Started](#getting-started)
- [Running Locally](#running-locally)
- [Environment Variables](#environment-variables)
- [Docker Instructions](#docker-instructions)
- [Deploy (Neon + Railway + Vercel)](#deploy-neon--railway--vercel)
- [Testing](#testing)
- [Documentation Links](#documentation-links)
- [Screenshots](#screenshots)
- [Roadmap](#roadmap)
- [Learning Outcomes](#learning-outcomes)
- [License](#license)
- [Author](#author)

## Documentation map

This README is the primary entry point. Supplementary documentation is indexed in **[docs/README.md](docs/README.md)**.

| Document | Description |
| -------- | ----------- |
| [docs/README.md](docs/README.md) | **Documentation index** — entry point for all supplementary docs |
| [Decisions.md](Decisions.md) | Technical decision record — alternatives, tradeoffs, and Git-history evidence for major choices (JWT, PostgreSQL, DTOs, testing, Docker, etc.) |
| [docs/deployment.md](docs/deployment.md) | **Production deploy** — Neon (DB), Railway (API), Vercel (UI) |
| [docs/packages.md](docs/packages.md) | Per-package responsibilities under `com.Harshit.note_app` (`controller`, `service`, `security`, and others) |
| [docs/project-tree.md](docs/project-tree.md) | Full repository tree with directory responsibilities and notes on legacy empty folders |
| [docs/diagram-audit.md](docs/diagram-audit.md) | Architecture and database diagram accuracy review against the current implementation |
| [docs/assets-plan.md](docs/assets-plan.md) | Planned diagrams and screenshots (PNG specs; assets not yet generated) |
| [SECURITY.md](SECURITY.md) | How to report vulnerabilities; deployment warnings (default JWT secret, credentials) |
| [note-app/HELP.md](note-app/HELP.md) | Module-level quick start, package-name note, and Spring reference links |

**Suggested reading order for new contributors:** [docs/README.md](docs/README.md) → Project Structure → [docs/packages.md](docs/packages.md) → Architecture Overview → [Decisions.md](Decisions.md) → API Overview → Getting Started.

---

## Project Overview

Notes API is a stateless REST service that lets registered users create, read, update, and delete their own notes. Authentication uses JSON Web Tokens (JWT). Notes support keyword search, pagination, and sorting.

A lightweight HTML/CSS/JS client is included for demos: served at `/` from Spring Boot for local/Docker use, and as a separate [`frontend/`](frontend/) app for **Vercel** in production. Interactive API docs are available via Swagger UI.

The application follows a layered design: controllers handle HTTP and use DTOs, services contain business logic, mappers translate between DTOs and entities, repositories manage persistence, and `model` holds JPA entities. See [Architecture Overview](#architecture-overview) for request flows and layer dependencies.

---

## Motivation

This project was built to practice backend fundamentals in a realistic setting:

- Designing a REST API with validation and consistent error responses
- Securing endpoints with Spring Security and JWT
- Scoping data access so users can only interact with their own notes
- Using JPA relationships, pagination, and custom queries
- Serving a same-origin web client for end-to-end demos
- Documenting the API with OpenAPI and testing core flows with unit and integration tests

---

## Features

| Feature | Description |
| ------- | ----------- |
| Basic web UI | Static HTML/CSS/JS client at `/` for register, login, and note CRUD |
| User registration | Create an account with unique username and email |
| User login | Authenticate with email and password; receive a JWT |
| Note CRUD | Create, read, update, and delete notes owned by the authenticated user |
| Keyword search | Filter notes by title or content (case-insensitive) via the `search` query parameter |
| Pagination | Page through note lists with `page` and `size` |
| Sorting | Sort by `title`, `createdAt`, or `updatedAt` (descending) |
| Input validation | Bean Validation on request DTOs (`@Valid`) |
| Exception handling | Centralized error responses via `@RestControllerAdvice` |
| Timestamps | `createdAt` and `updatedAt` managed automatically on entities |
| API documentation | Swagger UI and OpenAPI 3 spec via SpringDoc |

---

## Current Development Status

| Area | Status |
| ---- | ------ |
| Auth (register, login, JWT) | Complete |
| Note CRUD with user scoping | Complete |
| Search, pagination, sorting | Complete |
| Basic web UI (HTML/CSS/JS) | Complete — Spring `static/` + `frontend/` for Vercel |
| PostgreSQL configuration (default profile) | Complete |
| H2 profile for local development / tests | Complete |
| Unit and integration tests (25 tests) | Complete |
| Docker Compose (app + PostgreSQL 16) | Complete |
| Neon + Railway + Vercel deploy docs | Complete — see [docs/deployment.md](docs/deployment.md) |
| Maven Wrapper (`.mvn/`) | Missing — use system `mvn` locally; Docker build uses Maven in-image |
| License file | Not added yet |
| Refresh tokens, roles, rate limiting | Planned (not implemented) |

---

## Tech Stack

| Category | Technology | Version |
| -------- | ---------- | ------- |
| Language | Java | 21 |
| Framework | Spring Boot | 4.1.0 |
| Web | Spring Web MVC | (managed by Boot) |
| Persistence | Spring Data JPA / Hibernate | 7.4.1.Final |
| Security | Spring Security | 7.1.0 |
| JWT | jjwt | 0.12.6 |
| API docs | SpringDoc OpenAPI | 3.0.2 |
| Validation | Spring Validation | (managed by Boot) |
| Databases | PostgreSQL 16 (default), H2 (dev/test) | PostgreSQL driver / H2 2.4.240 |
| Build | Maven | 3.8+ |
| Testing | JUnit 5, Mockito | 6.0.3 / 5.23.0 |
| Frontend | Static HTML / CSS / JS | Served from `classpath:/static/` |
| Container | Docker, Docker Compose | `postgres:16-alpine`; Temurin JRE 21 Alpine |

---

## Project Structure

```text
notes_api/
├── .env.example
├── .gitignore
├── Decisions.md
├── SECURITY.md
├── docker-compose.yml
├── docs/
│   ├── assets-plan.md
│   ├── deployment.md
│   ├── diagram-audit.md
│   ├── packages.md
│   ├── project-tree.md
│   └── README.md
├── frontend/
│   ├── index.html
│   ├── config.js
│   ├── vercel.json
│   ├── write-config.js
│   ├── css/
│   └── js/
├── images/
├── README.md
└── note-app/
    ├── .dockerignore
    ├── Dockerfile
    ├── HELP.md
    ├── pom.xml
    ├── mvnw
    ├── mvnw.cmd
    └── src/
        ├── main/
        │   ├── java/com/Harshit/note_app/
        │   │   ├── config/
        │   │   ├── controller/
        │   │   ├── dto/
        │   │   ├── exception/
        │   │   ├── mapper/
        │   │   ├── model/
        │   │   ├── repository/
        │   │   ├── security/
        │   │   ├── service/
        │   │   └── NoteAppApplication.java
        │   └── resources/
        │       ├── application.properties
        │       ├── application-h2.properties
        │       └── static/
        │           ├── index.html
        │           ├── css/styles.css
        │           └── js/app.js
        └── test/
            ├── java/com/Harshit/note_app/
            │   ├── NotesApiIntegrationTest.java
            │   ├── NoteAppApplicationTests.java
            │   ├── security/JwtServiceTest.java
            │   └── service/
            └── resources/
                └── application-test.properties
```

<details>
<summary>Java source packages (<code>com.Harshit.note_app</code>)</summary>

```text
com.Harshit.note_app/
├── NoteAppApplication.java
├── config/          OpenApiConfig, PasswordEncoderConfig
├── controller/      AuthController, NoteController
├── dto/             Request/response DTOs, ApiErrorResponse
├── exception/       Custom exceptions, GlobalExceptionHandler
├── mapper/          NoteMapper, UserMapper
├── model/           Note, User
├── repository/      NoteRepository, UserRepository
├── security/        CustomUserDetails, CustomUserDetailsService, EmailPasswordAuthenticationProvider, JwtFilter, JwtService, SecurityConfig, SecurityUtils
└── service/         NoteService, UserService
```

</details>

---

## Architecture Overview

Architecture content in this section is ASCII text, not image files. A full comparison against the current codebase — including gaps, accuracy notes, and PNG checklist items — is in [`docs/diagram-audit.md`](docs/diagram-audit.md). There is **no booking flow** in this application; do not add booking diagrams.

### Layers and dependencies

Each layer depends only on layers below it. Controllers never access repositories or entities directly.

| Layer | Package | Responsibility | Depends on |
| ----- | ------- | -------------- | ---------- |
| Controller | `controller` | HTTP mapping, `@Valid` on request DTOs, response status codes | `service`, `dto` |
| Service | `service` | Business rules, ownership checks, orchestration | `repository`, `mapper`, `dto`, `model`, `security`, `exception` |
| Mapper | `mapper` | Manual conversion between DTOs and entities | `dto`, `model` |
| Repository | `repository` | Spring Data JPA persistence | `model` |
| Model | `model` | JPA entities (`User`, `Note`) | — |
| Security | `security` | JWT filter, auth provider, security config | `repository`, `exception` |
| Exception | `exception` | Custom exceptions and `GlobalExceptionHandler` | `dto` |
| Config | `config` | `BCryptPasswordEncoder` bean, OpenAPI definition | — |

### DTO usage

Controllers accept and return DTOs only. Entities are not exposed at the HTTP boundary.

| DTO | Direction | Used by |
| --- | --------- | ------- |
| `UserRegisterRequestDTO` | Request | `AuthController` → `UserService.register()` |
| `UserLoginRequestDTO` | Request | `AuthController` → `UserService.login()` |
| `UserResponseDTO` | Response | Registration |
| `UserLoginResponseDTO` | Response | Login (built in `UserService`, not via mapper) |
| `NoteRequestDTO` | Request | `NoteController` → `NoteService` |
| `NoteResponseDTO` | Response | All note endpoints |
| `ApiErrorResponse` | Response | `GlobalExceptionHandler` for all handled errors |

### Mapper usage

Mappers are `@Component` classes with manual field mapping (no MapStruct or Lombok).

| Mapper | Method | Purpose |
| ------ | ------ | ------- |
| `UserMapper` | `toEntity(UserRegisterRequestDTO)` | Create `User` on registration (password set separately in service) |
| `UserMapper` | `toResponseDTO(User)` | Map saved user to `UserResponseDTO` |
| `NoteMapper` | `toEntity(NoteRequestDTO)` | Create `Note` on insert |
| `NoteMapper` | `toResponseDTO(Note)` | Map `Note` to `NoteResponseDTO` |

`NoteService.updateNote()` updates entity fields directly from `NoteRequestDTO` without calling the mapper. `UserService.login()` constructs `UserLoginResponseDTO` directly.

### Servlet request flow

`JwtFilter` is registered **inside** `SecurityFilterChain` (before `UsernamePasswordAuthenticationFilter`), not as a separate step above it. The filter runs on **every** request; on public routes it simply continues when no `Authorization: Bearer` header is present.

```text
HTTP Request
  │
  ▼
SecurityFilterChain
  ├── JwtFilter (all routes)
  │     ├── No Bearer header → continue without setting authentication
  │     ├── Valid JWT → load CustomUserDetails, set SecurityContext
  │     └── Invalid JWT → clear SecurityContext, continue chain
  ├── Authorization check (SecurityConfig rules)
  │     ├── Public: POST /api/auth/register, POST /api/auth/login, Swagger/OpenAPI paths
  │     └── All other routes → require authenticated principal (includes /api/notes/** and /h2-console when h2 profile is active)
  └── On auth failure → GlobalExceptionHandler.commence() → 401 ApiErrorResponse
      On access denied  → GlobalExceptionHandler.handle()  → 403 ApiErrorResponse
  │
  ▼
DispatcherServlet → Controller
  │
  ▼
Service → Mapper (where used) → Repository → Database
  │
  ▼
Response DTO (or void for DELETE 204)
```

### Registration flow

```text
POST /api/auth/register
  → AuthController (@Valid UserRegisterRequestDTO)
       └── Validation failure → GlobalExceptionHandler → 400
  → UserService.register()
       ├── UserRepository.existsByUsername / existsByEmail
       ├── UserMapper.toEntity()
       ├── PasswordEncoder.encode() (BCrypt)
       └── UserRepository.save() → UserMapper.toResponseDTO()
  → 201 UserResponseDTO (no JWT issued — client must call /api/auth/login)
```

On duplicate username or email, `UserService` throws `DuplicateUsernameException` or `DuplicateEmailException`, handled by `GlobalExceptionHandler` → `409`.

### Login flow

```text
POST /api/auth/login
  → AuthController (validates UserLoginRequestDTO)
  → UserService.login()
       → AuthenticationManager.authenticate()
            → EmailPasswordAuthenticationProvider
                 → CustomUserDetailsService.loadUserByUsername(email)
                      → UserRepository.findByEmail()
                 → PasswordEncoder.matches() (BCrypt)
       → JwtService.generateToken()  (JWT subject = email)
  → 200 UserLoginResponseDTO
```

On bad credentials, `BadCredentialsException` propagates as `AuthenticationException` → `GlobalExceptionHandler` → `401`.

### Protected note request flow

```text
GET|POST|PUT|DELETE /api/notes/**
  → JwtFilter sets SecurityContext from Bearer token
  → Authorization check passes
  → NoteController (validates NoteRequestDTO on write operations)
  → NoteService
       ├── SecurityUtils.getCurrentUser() → CustomUserDetails → User entity
       ├── createNote: NoteMapper.toEntity() → note.setUser(currentUser) → save
       ├── findOwnedNote() uses NoteRepository.findByIdAndUser() for get/update/delete
       ├── list (GET): optional keyword → searchNotesByUser() or findByUser() with page/size/sortBy
       ├── update: set title/content directly on owned note → save
       └── delete: delete owned note → 204 No Content
  → Repository → JPA → Database
  → NoteMapper.toResponseDTO() on read/write responses (not on delete)
```

If no valid JWT is present, the authorization check fails before the controller is reached. This flow is simplified; query parameters for list/search are documented under [GET /api/notes](#get-apinotes).

### Exception flow

```text
Controller or Service throws exception
  │
  ├── MethodArgumentNotValidException  → 400  (comma-joined field messages)
  ├── InvalidSortFieldException        → 400
  ├── ResourceNotFoundException        → 404
  ├── DuplicateUsername/EmailException → 409
  ├── AuthenticationException          → 401  ("Invalid email or password")
  └── AccessDeniedException            → 403

Filter-chain auth failures (no/invalid JWT on protected routes):
  → GlobalExceptionHandler.commence()  → 401  ("Authentication required")
  → GlobalExceptionHandler.handle()    → 403  ("Access denied")

All handled errors return ApiErrorResponse (timestamp, status, error, message, path).
```

Unhandled exceptions (e.g. `IllegalStateException` from `SecurityUtils` on a missing principal) are not mapped and will produce a default Spring error response.

---

## Database Overview

### Entities

| Entity | Table | Key fields | Relationships |
| ------ | ----- | ---------- | ------------- |
| `User` | `users` | `id`, `username` (unique), `email` (unique), `password`, `createdAt` | One user owns many notes (no inverse `@OneToMany` on `User`) |
| `Note` | `notes` | `id`, `title`, `content` (TEXT), `createdAt`, `updatedAt`, `user_id` (FK, NOT NULL) | `@ManyToOne` → `User` via `user_id` |

Passwords are stored as BCrypt hashes. The `password` field is excluded from JSON serialization.

**Physical columns:** Hibernate maps camelCase fields to snake_case columns (e.g. `createdAt` → `created_at`, `updatedAt` → `updated_at`). The relationship is **unidirectional** — only `Note` references `User`; there is no `notes` collection on the `User` entity. Deleting users with existing notes is not handled in application code (no cascade rules).

### Profiles

| Profile | Database | DDL | Config file |
| ------- | -------- | --- | ----------- |
| *(default)* | PostgreSQL | `update` | `application.properties` |
| `h2` | H2 in-memory (`notes_db`) | `update` | `application-h2.properties` |
| `test` | H2 in-memory (`notes_test`) | `create-drop` | `application-test.properties` |

Schema is managed by Hibernate (`ddl-auto`). No Flyway or Liquibase migrations are included.

The `h2` profile enables the H2 console at `/h2-console`, but that path is not listed as a public endpoint in `SecurityConfig`, so it requires authentication like other protected routes.

---

## Authentication Overview

Authentication is **stateless** and **JWT-based**. Request flows are documented in [Architecture Overview](#architecture-overview).

| Aspect | Implementation |
| ------ | -------------- |
| Registration | `POST /api/auth/register` — public; no JWT required |
| Login | `POST /api/auth/login` — public; returns JWT |
| Token usage | `Authorization: Bearer <token>` on protected endpoints |
| Token subject | User email (`CustomUserDetails.getUsername()` returns email) |
| Principal type | `CustomUserDetails` wrapping the `User` entity |
| Password storage | BCrypt via `PasswordEncoder` bean in `PasswordEncoderConfig` |
| Session | Disabled (`SessionCreationPolicy.STATELESS`) |
| CSRF | Disabled (stateless API) |
| Roles | Not implemented (`getAuthorities()` returns empty) |

### JWT reference

How clients obtain, send, and reason about access tokens. Implementation: `JwtService`, `JwtFilter`, `CustomUserDetails`.

#### Obtaining a token

| Step | Detail |
| ---- | ------ |
| Endpoint | `POST /api/auth/login` only — registration does **not** return a token |
| Response field | `UserLoginResponseDTO.token` (string) |
| After login | Client stores the token and sends it on subsequent protected requests |

#### Authorization header

Protected endpoints require:

```http
Authorization: Bearer <token>
```

| Rule | Detail |
| ---- | ------ |
| Scheme | `Bearer` (case-sensitive in practice; filter checks `startsWith("Bearer ")`) |
| Token position | Characters after the first space — no quotes, no `Bearer` prefix in the token value |
| Missing header | Request continues without authentication → `401` on protected routes |
| Invalid / expired token | `SecurityContext` cleared → `401` on protected routes (`Authentication required`) |

Example:

```http
GET /api/notes HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

In Swagger UI, use the **Authorize** dialog and enter `Bearer <token>` (or paste the raw token if the UI adds the prefix automatically).

#### Token format and claims

Tokens are compact JWS strings produced by **jjwt** (`io.jsonwebtoken`), signed with **HMAC-SHA256** using the configured `jwt.secret` key.

| Claim | JWT name | Value |
| ----- | -------- | ----- |
| Subject | `sub` | User **email** (not username). `CustomUserDetails.getUsername()` returns email; this becomes the JWT subject at login. |
| Issued at | `iat` | Timestamp when the token was created |
| Expiration | `exp` | Timestamp when the token becomes invalid |

**Not included:** `username`, roles, scopes, `jti`, or other custom claims. The API does not embed permissions in the token.

On each protected request, `JwtFilter` extracts `sub`, loads the user by email via `CustomUserDetailsService`, and validates signature and expiration with `JwtService.isTokenValid()`.

#### Expiration

| Setting | Property / env var | Default |
| ------- | ------------------ | ------- |
| Lifetime | `jwt.expiration` / `JWT_EXPIRATION` | `86400000` ms (**24 hours**) |

Expiration is absolute — there is no sliding window or silent renewal. When `exp` is in the past, the token is rejected.

Configure for deployment:

```bash
export JWT_EXPIRATION=3600000   # 1 hour, in milliseconds
```

See [Environment Variables](#environment-variables) and [SECURITY.md](SECURITY.md) for signing secret requirements.

#### Refresh tokens

**Not supported.** This API issues a single access token per login. There is:

- No `POST /api/auth/refresh` (or similar) endpoint
- No refresh token in `UserLoginResponseDTO`
- No server-side token revocation list

When an access token expires, the client must call `POST /api/auth/login` again with email and password. Stolen tokens remain valid until `exp` — see [SECURITY.md](SECURITY.md).

#### Quick reference

| Topic | Behavior |
| ----- | -------- |
| Obtain token | `POST /api/auth/login` → `token` field |
| Send token | `Authorization: Bearer <token>` |
| Subject (`sub`) | User email |
| Default lifetime | 24 hours (`JWT_EXPIRATION=86400000`) |
| Signing | HMAC-SHA256, key from `JWT_SECRET` |
| Refresh | Not implemented — re-login required |

### Public endpoints

Configured explicitly in `SecurityConfig`:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `/`, `/index.html`, `/css/**`, `/js/**`, `/favicon.ico` (basic web UI)
- `/swagger-ui.html`, `/swagger-ui/**`
- `/v3/api-docs/**`

### Protected endpoints

- All `/api/notes/**` routes require a valid JWT
- `/h2-console` (enabled by the `h2` profile) also requires authentication

Unauthenticated requests to protected routes receive `401` with an `ApiErrorResponse` body via `GlobalExceptionHandler` as the `AuthenticationEntryPoint`.

Role-based access control is not implemented. All authenticated users have the same access level.

---

## API Overview

**Base URL:** `http://localhost:8080`

**Content type:** `application/json` for all request and response bodies.

**Authentication header** (protected endpoints only):

```http
Authorization: Bearer <jwt-token>
```

Full token contract (claims, expiry, refresh policy): [JWT reference](#jwt-reference).

**Error body** (all documented error responses): `ApiErrorResponse`

```json
{
  "timestamp": "2026-07-09T10:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Title is mandatory, Content is mandatory",
  "path": "/api/notes"
}
```

The API exposes **7 endpoints** across 2 controllers: `AuthController` and `NoteController`. There is no separate `/api/notes/search` route; search is a query parameter on `GET /api/notes`.

---

### POST `/api/auth/register`

| | |
| --- | --- |
| **Method** | `POST` |
| **Purpose** | Create a new user account with a unique username and email |
| **Authentication** | Not required |

**Request body** (`UserRegisterRequestDTO`):

```json
{
  "username": "demo",
  "email": "demo@example.com",
  "password": "password123"
}
```

| Field | Rules |
| ----- | ----- |
| `username` | Required; 3–50 characters |
| `email` | Required; valid email format |
| `password` | Required; minimum 6 characters |

**Response body** (`UserResponseDTO`, `201 Created`):

```json
{
  "id": 1,
  "username": "demo",
  "email": "demo@example.com",
  "createdAt": "2026-07-09T10:30:45"
}
```

**Status codes**

| Code | When |
| ---- | ---- |
| `201` | User created successfully |
| `400` | Request validation failed |
| `409` | Username or email already exists |

**Possible errors**

| Status | `message` example |
| ------ | ----------------- |
| `400` | `Username is mandatory`, `Email must be valid`, `Password must be at least 6 characters` (comma-joined if multiple) |
| `409` | `Username is already taken` |
| `409` | `Email is already registered` |

---

### POST `/api/auth/login`

| | |
| --- | --- |
| **Method** | `POST` |
| **Purpose** | Authenticate with email and password; receive a JWT for protected endpoints |
| **Authentication** | Not required |

**Request body** (`UserLoginRequestDTO`):

```json
{
  "email": "demo@example.com",
  "password": "password123"
}
```

| Field | Rules |
| ----- | ----- |
| `email` | Required; valid email format |
| `password` | Required |

**Response body** (`UserLoginResponseDTO`, `200 OK`):

```json
{
  "token": "<jwt>",
  "username": "demo",
  "email": "demo@example.com"
}
```

**Status codes**

| Code | When |
| ---- | ---- |
| `200` | Login successful |
| `400` | Request validation failed |
| `401` | Invalid email or password |

**Possible errors**

| Status | `message` |
| ------ | --------- |
| `400` | `Email is mandatory`, `Password is mandatory`, `Email must be valid` (comma-joined if multiple) |
| `401` | `Invalid email or password` |

---

### POST `/api/notes`

| | |
| --- | --- |
| **Method** | `POST` |
| **Purpose** | Create a new note owned by the authenticated user |
| **Authentication** | Required (`Bearer` JWT) |

**Request body** (`NoteRequestDTO`):

```json
{
  "title": "My first note",
  "content": "Hello from Notes API."
}
```

| Field | Rules |
| ----- | ----- |
| `title` | Required; maximum 100 characters |
| `content` | Required |

**Response body** (`NoteResponseDTO`, `201 Created`):

```json
{
  "id": 1,
  "title": "My first note",
  "content": "Hello from Notes API.",
  "createdAt": "2026-07-09T10:30:45",
  "updatedAt": "2026-07-09T10:30:45"
}
```

**Status codes**

| Code | When |
| ---- | ---- |
| `201` | Note created successfully |
| `400` | Request validation failed |
| `401` | Missing or invalid JWT |

**Possible errors**

| Status | `message` example |
| ------ | ----------------- |
| `400` | `Title is mandatory`, `Content is mandatory`, `Title must not exceed 100 characters` |
| `401` | `Authentication required` (no/invalid token) |

---

### GET `/api/notes`

| | |
| --- | --- |
| **Method** | `GET` |
| **Purpose** | List the authenticated user's notes with optional search, pagination, and sorting |
| **Authentication** | Required (`Bearer` JWT) |

**Request body** | None

**Query parameters**

| Parameter | Default | Description |
| --------- | ------- | ----------- |
| `search` | — | Optional keyword matched against `title` and `content` (case-insensitive) |
| `page` | `0` | Zero-based page index |
| `size` | `10` | Number of notes per page |
| `sortBy` | `updatedAt` | Sort field: `title`, `createdAt`, or `updatedAt` (descending) |

**Response body** (`Page<NoteResponseDTO>`, `200 OK`):

```json
{
  "content": [
    {
      "id": 1,
      "title": "My first note",
      "content": "Hello from Notes API.",
      "createdAt": "2026-07-09T10:30:45",
      "updatedAt": "2026-07-09T10:30:45"
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 10, "offset": 0, "paged": true, "unpaged": false },
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "numberOfElements": 1,
  "first": true,
  "last": true,
  "empty": false
}
```

**Status codes**

| Code | When |
| ---- | ---- |
| `200` | Notes retrieved successfully |
| `400` | Invalid `sortBy` value |
| `401` | Missing or invalid JWT |

**Possible errors**

| Status | `message` example |
| ------ | ----------------- |
| `400` | `Invalid sort field: invalidField` |
| `401` | `Authentication required` |

---

### GET `/api/notes/{id}`

| | |
| --- | --- |
| **Method** | `GET` |
| **Purpose** | Retrieve a single note by ID (must belong to the authenticated user) |
| **Authentication** | Required (`Bearer` JWT) |

**Path parameters**

| Parameter | Type | Description |
| --------- | ---- | ----------- |
| `id` | `Long` | Note ID |

**Request body** | None

**Response body** (`NoteResponseDTO`, `200 OK`):

```json
{
  "id": 1,
  "title": "My first note",
  "content": "Hello from Notes API.",
  "createdAt": "2026-07-09T10:30:45",
  "updatedAt": "2026-07-09T10:30:45"
}
```

**Status codes**

| Code | When |
| ---- | ---- |
| `200` | Note found |
| `401` | Missing or invalid JWT |
| `404` | Note not found or not owned by the user |

**Possible errors**

| Status | `message` example |
| ------ | ----------------- |
| `401` | `Authentication required` |
| `404` | `Note not found with id 5` |

---

### PUT `/api/notes/{id}`

| | |
| --- | --- |
| **Method** | `PUT` |
| **Purpose** | Update a note owned by the authenticated user |
| **Authentication** | Required (`Bearer` JWT) |

**Path parameters**

| Parameter | Type | Description |
| --------- | ---- | ----------- |
| `id` | `Long` | Note ID |

**Request body** (`NoteRequestDTO`):

```json
{
  "title": "Updated title",
  "content": "Updated content."
}
```

| Field | Rules |
| ----- | ----- |
| `title` | Required; maximum 100 characters |
| `content` | Required |

**Response body** (`NoteResponseDTO`, `200 OK`):

```json
{
  "id": 1,
  "title": "Updated title",
  "content": "Updated content.",
  "createdAt": "2026-07-09T10:30:45",
  "updatedAt": "2026-07-09T11:00:00"
}
```

**Status codes**

| Code | When |
| ---- | ---- |
| `200` | Note updated successfully |
| `400` | Request validation failed |
| `401` | Missing or invalid JWT |
| `404` | Note not found or not owned by the user |

**Possible errors**

| Status | `message` example |
| ------ | ----------------- |
| `400` | `Title is mandatory`, `Content is mandatory` |
| `401` | `Authentication required` |
| `404` | `Note not found with id 5` |

---

### DELETE `/api/notes/{id}`

| | |
| --- | --- |
| **Method** | `DELETE` |
| **Purpose** | Delete a note owned by the authenticated user |
| **Authentication** | Required (`Bearer` JWT) |

**Path parameters**

| Parameter | Type | Description |
| --------- | ---- | ----------- |
| `id` | `Long` | Note ID |

**Request body** | None

**Response body** | Empty (`204 No Content`)

**Status codes**

| Code | When |
| ---- | ---- |
| `204` | Note deleted successfully |
| `401` | Missing or invalid JWT |
| `404` | Note not found or not owned by the user |

**Possible errors**

| Status | `message` example |
| ------ | ----------------- |
| `401` | `Authentication required` |
| `404` | `Note not found with id 5` |

---

### Endpoint summary

| Method | URL | Auth | Success |
| ------ | --- | ---- | ------- |
| `POST` | `/api/auth/register` | No | `201` |
| `POST` | `/api/auth/login` | No | `200` |
| `POST` | `/api/notes` | JWT | `201` |
| `GET` | `/api/notes` | JWT | `200` |
| `GET` | `/api/notes/{id}` | JWT | `200` |
| `PUT` | `/api/notes/{id}` | JWT | `200` |
| `DELETE` | `/api/notes/{id}` | JWT | `204` |

Interactive documentation: [Swagger UI](http://localhost:8080/swagger-ui.html) (public, no JWT required to view).

---

## Web UI

Two copies of the same client:

| Location | Use |
| -------- | --- |
| `note-app/src/main/resources/static/` | Local / Docker — same-origin at **http://localhost:8080/** |
| [`frontend/`](frontend/) | **Vercel** production UI (set `NOTES_API_BASE` to the Railway API URL) |

| Capability | UI support |
| ---------- | ---------- |
| Register / log in | Auth panel with tabs |
| Create / update / delete notes | Editor panel |
| List notes | Sidebar |
| Search, sort, pagination | Toolbar controls |
| Log out | Session bar |

The browser stores the JWT in `localStorage` and sends `Authorization: Bearer <token>` on note requests. Production (Vercel → Railway) requires CORS — see [Deploy](#deploy-neon--railway--vercel).

---

## Getting Started

### Prerequisites

- **Java 21** (JDK)
- **Maven 3.8+** (system install — `.mvn/` wrapper files are not in this repo)
- **Docker Desktop / Docker Engine** (recommended) **or** a local PostgreSQL 16+ instance
- Optional: use the `h2` profile to skip PostgreSQL entirely

### Clone

```bash
git clone https://github.com/HarsshitSri/notes_api.git
# or: git clone git@github.com:HarsshitSri/notes_api.git
cd notes_api
```

### Quick start (Docker Compose)

```bash
cp .env.example .env    # optional
docker compose up --build
```

Then open:

| Resource | URL |
| -------- | --- |
| Web UI | http://localhost:8080/ |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

Default DB credentials match `.env.example` (`notes_user` / `notes_pass`, database `notes_db`). Change `JWT_SECRET` before any shared deployment — see [SECURITY.md](SECURITY.md).

---

## Running Locally

Application commands below run from `note-app/` unless noted.

### Option A — H2 (fastest, no external database)

```bash
cd note-app
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

| Resource | URL |
| -------- | --- |
| Web UI | http://localhost:8080/ |
| Swagger UI | http://localhost:8080/swagger-ui.html |

### Option B — PostgreSQL (default profile)

1. Start PostgreSQL 16 with database `notes_db` and user/password matching the defaults, **or** run only the DB container:

```bash
# from repository root
docker compose up -d postgres
```

2. Run the app (default JDBC URL: `jdbc:postgresql://localhost:5432/notes_db`):

```bash
cd note-app
mvn spring-boot:run
```

If host port **5432** is already in use, remap Compose (e.g. `"5433:5432"`) and set:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/notes_db
```

### Example API workflow

```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","email":"demo@example.com","password":"password123"}'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"password123"}'

# 3. Create a note (replace <token>)
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"My first note","content":"Hello from Notes API."}'
```

Or use the [Web UI](#web-ui) at http://localhost:8080/ instead of curl.

---

## Environment Variables

### Application

| Variable | Maps to | Default |
| -------- | ------- | ------- |
| `PORT` | `server.port` | `8080` (Railway sets this automatically) |
| `SPRING_DATASOURCE_URL` | JDBC URL | `jdbc:postgresql://localhost:5432/notes_db` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `notes_user` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `notes_pass` |
| `SPRING_JPA_SHOW_SQL` | SQL logging | `false` |
| `JWT_SECRET` | `jwt.secret` | Dev default — **override in production** |
| `JWT_EXPIRATION` | `jwt.expiration` (ms) | `86400000` (24 hours) |
| `CORS_ALLOWED_ORIGINS` | `cors.allowed-origins` | Empty → allows `http://localhost:*` / `http://127.0.0.1:*` |

### Docker Compose (`.env.example`)

| Variable | Description | Default |
| -------- | ----------- | ------- |
| `POSTGRES_DB` | Database name | `notes_db` |
| `POSTGRES_USER` | Application DB user | `notes_user` |
| `POSTGRES_PASSWORD` | Application DB password | `notes_pass` |
| `JWT_SECRET` | JWT signing secret | Same as application default |
| `JWT_EXPIRATION` | Token lifetime (ms) | `86400000` |
| `CORS_ALLOWED_ORIGINS` | Browser origins for split UI (optional locally) | unset |

```bash
cp .env.example .env   # optional: customize before docker compose up
```

---

## Docker Instructions

`docker-compose.yml` at the repository root defines two services:

```text
┌─────────────────────────────┐       ┌──────────────────────────────┐
│  app (notes-api) :8080      │──────▶│  postgres (notes-postgres)   │
│  Spring Boot JAR + static UI│ JDBC  │  :5432  volume: postgres_data│
│  env: SPRING_DATASOURCE_*,  │       │  healthcheck → depends_on    │
│       JWT_SECRET,           │       │                              │
│       JWT_EXPIRATION        │       │                              │
└─────────────────────────────┘       └──────────────────────────────┘
```

The `note-app/Dockerfile` uses a **multi-stage build**: `maven:3.9-eclipse-temurin-21-alpine` compiles the JAR; `eclipse-temurin:21-jre-alpine` runs it. The Maven Wrapper is not required for the image build.

### Start

```bash
cp .env.example .env    # optional
docker compose up --build
```

| Resource | URL |
| -------- | --- |
| Web UI | http://localhost:8080/ |
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

### Useful commands

```bash
docker compose up --build -d   # detached mode
docker compose logs -f app     # application logs (service name: app)
docker compose down            # stop services
docker compose down -v         # stop and remove database volume
```

### Build details

| Stage | Image | Purpose |
| ----- | ----- | ------- |
| `build` | `maven:3.9-eclipse-temurin-21-alpine` | `mvn -DskipTests package` |
| runtime | `eclipse-temurin:21-jre-alpine` | `java -jar app.jar` on port 8080 |

PostgreSQL must become healthy before the app container starts (`depends_on: service_healthy`). Override `JWT_SECRET` and database credentials via `.env` before any shared deployment — see [SECURITY.md](SECURITY.md).

---

## Deploy (Neon + Railway + Vercel)

Intended production stack:

| Layer | Platform | Artifact |
| ----- | -------- | -------- |
| Database | [Neon](https://neon.tech) | PostgreSQL (`sslmode=require`) |
| API | [Railway](https://railway.app) | `note-app` Dockerfile |
| UI | [Vercel](https://vercel.com) | `frontend/` static app |

Full step-by-step (JDBC URL, env vars, CORS, troubleshooting): **[docs/deployment.md](docs/deployment.md)**.

Quick outline:

1. Create a Neon database → set `SPRING_DATASOURCE_*` on Railway.
2. Deploy `note-app` on Railway with a strong `JWT_SECRET`.
3. Deploy `frontend/` on Vercel with `NOTES_API_BASE=<railway-https-url>`.
4. Set `CORS_ALLOWED_ORIGINS=<vercel-https-url>` on Railway and redeploy.

---

## Testing

```bash
cd note-app
mvn clean test
```

Requires **Java 21**. Integration tests use the `test` profile with an in-memory H2 database.

| Test class | Tests | Coverage |
| ---------- | ----- | -------- |
| `NotesApiIntegrationTest` | 5 | Auth + note lifecycle, validation errors, unauthorized access |
| `UserServiceTest` | 5 | Registration, duplicates, login success/failure |
| `NoteServiceTest` | 9 | CRUD, search, sorting, ownership, not-found cases |
| `JwtServiceTest` | 5 | Token generation, validation, expiration |
| `NoteAppApplicationTests` | 1 | Application class presence |

**Total: 25 tests**

HTTP-level tests for pagination, search, sorting, and duplicate-registration (`409`) responses are not yet covered.

---

## Documentation Links

| Resource | URL |
| -------- | --- |
| Swagger UI (local) | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON (local) | http://localhost:8080/v3/api-docs |
| Module help | [note-app/HELP.md](note-app/HELP.md) |
| Spring Boot 4.1 reference | https://docs.spring.io/spring-boot/4.1.0/reference/ |
| Spring Security reference | https://docs.spring.io/spring-security/reference/ |
| SpringDoc OpenAPI | https://springdoc.org/ |

Controller methods include `@Operation` and `@ApiResponses` annotations. The OpenAPI document title in code is **Note API** (see `OpenApiConfig.java`).

---

## Screenshots

> **Historical only.** These images were captured before JWT auth and the current response shapes. Prefer the [Web UI](#web-ui), [Swagger UI](http://localhost:8080/swagger-ui.html), or [API Overview](#api-overview) for current behavior.

<details>
<summary>Show outdated Postman screenshots</summary>

| Image | What it shows | Differs from current API |
| ----- | ------------- | ------------------------ |
| `create-note.png` | `POST /api/notes` | Shows `200 OK`; current API returns `201 Created` |
| `get-all-notes.png` | `GET /api/notes` | Shows a plain JSON array; current API returns a paginated `Page` object |
| `pagination-and-sorting.png` | Paginated list with query params | Largely consistent with current pagination |
| `validation.png` | Validation error on empty fields | Shows per-field JSON map; current API returns `ApiErrorResponse` |

![Create Note](images/create-note.png)

![Retrieve All Notes](images/get-all-notes.png)

![Pagination and sorting](images/pagination-and-sorting.png)

![Validation error](images/validation.png)

</details>

---

## Roadmap

| Item | Status |
| ---- | ------ |
| Add `LICENSE` file | Planned |
| Expand integration test coverage (pagination, search, 409 conflicts) | Planned |
| CI/CD pipeline | Planned |
| Refresh tokens | Planned |
| Email verification | Planned |
| Rate limiting | Planned |
| Database migrations (Flyway or Liquibase) | Planned |
| Role-based access control | Planned |
| CORS configuration for external SPA | Complete — `CORS_ALLOWED_ORIGINS` |
| Neon + Railway + Vercel production path | Documented |
| Replace outdated API screenshots | Planned |

---

## Learning Outcomes

Working on this project covered:

- Structuring a Spring Boot application into controllers, services, repositories, DTOs, and mappers
- Implementing stateless JWT authentication with Spring Security filters and a custom authentication provider
- Enforcing per-user data isolation at the service and repository layers
- Writing JPQL queries for search and combining them with Spring Data pagination
- Centralizing API error handling with `@RestControllerAdvice` and a consistent error DTO
- Documenting REST endpoints with SpringDoc OpenAPI annotations
- Serving a same-origin static web client for demos
- Writing unit tests with Mockito and integration tests with `@SpringBootTest` and MockMvc
- Packaging the stack with Docker Compose (Spring Boot + PostgreSQL)

---

## License

No license file is included in this repository yet. All rights reserved by the author until a license is added.

When a `LICENSE` file is added, update the license badge at the top of this README (e.g. `https://img.shields.io/badge/License-MIT-blue` for MIT).

---

## Author

**Harsshit Sri** — [github.com/HarsshitSri](https://github.com/HarsshitSri)

# Package Structure — `com.Harshit.note_app`

Concise reference for each source package under `note-app/src/main/java/com/Harshit/note_app/`.

---

## `controller/`

**Purpose:** REST controllers that handle HTTP requests and responses.

**Contains:** `AuthController` (register, login), `NoteController` (note CRUD, list with search/pagination/sorting).

**Responsibilities:**
- Map URLs and HTTP methods to service calls
- Apply `@Valid` on request DTOs
- Set HTTP status codes (`201`, `204`, etc.)
- Do not access repositories or entities directly

---

## `service/`

**Purpose:** Business logic and transaction orchestration.

**Contains:** `UserService` (registration, login, duplicate checks), `NoteService` (CRUD, search, pagination, ownership enforcement).

**Responsibilities:**
- Enforce rules (unique username/email, valid sort fields, note ownership)
- Coordinate repositories, mappers, and `SecurityUtils`
- Return DTOs to controllers, never entities

---

## `repository/`

**Purpose:** Data access via Spring Data JPA.

**Contains:** `UserRepository`, `NoteRepository`.

**Responsibilities:**
- CRUD and query methods derived from `JpaRepository`
- User-scoped note queries (`findByUser`, `findByIdAndUser`, `searchNotesByUser`)
- No business logic

---

## `model/`

**Purpose:** JPA entity classes mapped to database tables.

**Contains:** `User` (`users` table), `Note` (`notes` table, `@ManyToOne` to `User`).

**Responsibilities:**
- Define schema through annotations (`@Entity`, `@Table`, `@JoinColumn`)
- Manage timestamps via `@PrePersist` / `@PreUpdate`
- Not exposed at the HTTP boundary

---

## `dto/`

**Purpose:** API request and response contracts, decoupled from persistence.

**Contains:**
- Request: `UserRegisterRequestDTO`, `UserLoginRequestDTO`, `NoteRequestDTO`
- Response: `UserResponseDTO`, `UserLoginResponseDTO`, `NoteResponseDTO`
- Errors: `ApiErrorResponse`

**Responsibilities:**
- Carry validation annotations (`@NotBlank`, `@Email`, `@Size`)
- Define the JSON shape clients send and receive

---

## `mapper/`

**Purpose:** Convert between DTOs and entities.

**Contains:** `UserMapper`, `NoteMapper`.

**Responsibilities:**
- `toEntity()` for create operations
- `toResponseDTO()` for read operations
- Manual field mapping (`@Component` classes, no code generation)

---

## `security/`

**Purpose:** Authentication, authorization, and JWT handling.

**Contains:**
- `SecurityConfig` — filter chain, public vs protected routes
- `JwtFilter` — extract and validate Bearer tokens
- `JwtService` — token generation and validation
- `EmailPasswordAuthenticationProvider` — email/password login
- `CustomUserDetailsService`, `CustomUserDetails` — load user by email
- `SecurityUtils` — resolve current `User` from `SecurityContext`

**Responsibilities:**
- Stateless JWT authentication
- BCrypt password verification at login
- Populate `SecurityContext` for downstream services

---

## `config/`

**Purpose:** Spring `@Configuration` beans not tied to a domain layer.

**Contains:** `PasswordEncoderConfig` (BCrypt bean), `OpenApiConfig` (Swagger/OpenAPI, Bearer JWT scheme).

**Responsibilities:**
- Register cross-cutting infrastructure beans
- Define API documentation metadata

---

## `exception/`

**Purpose:** Domain-specific exceptions and centralized error handling.

**Contains:**
- `GlobalExceptionHandler` — `@RestControllerAdvice`, maps exceptions to `ApiErrorResponse`; also serves as Spring Security entry point and access-denied handler
- `ResourceNotFoundException`, `DuplicateUsernameException`, `DuplicateEmailException`, `InvalidSortFieldException`

**Responsibilities:**
- Translate thrown exceptions into consistent HTTP error responses
- Handle validation failures from `@Valid`

---

## Packages not present

| Requested | Status |
| --------- | ------ |
| `entity/` | Not used — entities live in `model/` |
| `util/` | Does not exist — no shared utility package in this project |

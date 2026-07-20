# Project Tree

Accurate layout of the `notes_api` repository. Excludes `target/`, `.git/`, and IDE folders.

```text
notes_api/
├── .env.example
├── .gitignore
├── Decisions.md
├── README.md
├── SECURITY.md
├── docker-compose.yml
├── docs/
│   ├── README.md
│   ├── assets-plan.md
│   ├── diagram-audit.md
│   ├── packages.md
│   └── project-tree.md
├── images/
│   ├── create-note.png
│   ├── get-all-notes.png
│   ├── pagination-and-sorting.png
│   └── validation.png
└── note-app/
    ├── .dockerignore
    ├── Dockerfile
    ├── HELP.md
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/Harshit/note_app/
        │   │   ├── NoteAppApplication.java
        │   │   ├── config/
        │   │   ├── controller/
        │   │   ├── dto/
        │   │   ├── exception/
        │   │   ├── mapper/
        │   │   ├── model/
        │   │   ├── repository/
        │   │   ├── security/
        │   │   └── service/
        │   └── resources/
        │       ├── application.properties
        │       ├── application-h2.properties
        │       └── static/
        │           ├── index.html
        │           ├── css/styles.css
        │           └── js/app.js
        └── test/
            ├── java/com/Harshit/note_app/
            │   ├── security/
            │   └── service/
            └── resources/
                └── application-test.properties
```

## Directory responsibilities

### Repository root

| Path | Responsibility |
| ---- | -------------- |
| `README.md` | Primary project documentation: setup, API reference, architecture, web UI |
| `Decisions.md` | Record of major technical decisions and tradeoffs |
| `SECURITY.md` | Security policy — vulnerability reporting and deployment warnings |
| `docker-compose.yml` | Orchestrates PostgreSQL and the Spring Boot application for containerized runs |
| `.env.example` | Template for Docker Compose environment variables (database credentials, JWT settings) |
| `.gitignore` | Excludes build output, IDE files, and local secrets from version control |
| `docs/` | Supplementary documentation — start at [`docs/README.md`](README.md) |
| `images/` | Historical API screenshots (outdated; see README disclaimer) |
| `note-app/` | Spring Boot application module (source, build config, container image) |

### `note-app/`

| Path | Responsibility |
| ---- | -------------- |
| `pom.xml` | Maven build definition, dependencies, and Java 21 compiler settings |
| `mvnw`, `mvnw.cmd` | Maven Wrapper scripts (`.mvn/` wrapper files are not present in the repo) |
| `Dockerfile` | Multi-stage image build: Maven compile → JRE 21 Alpine runtime |
| `.dockerignore` | Files excluded from the Docker build context |
| `HELP.md` | Module-level getting started and Spring reference links |

### `note-app/src/main/`

| Path | Responsibility |
| ---- | -------------- |
| `java/.../config/` | Spring configuration beans (BCrypt encoder, OpenAPI/Swagger) |
| `java/.../controller/` | REST endpoints (`AuthController`, `NoteController`) |
| `java/.../dto/` | HTTP request/response and error DTOs |
| `java/.../exception/` | Custom exceptions and `GlobalExceptionHandler` |
| `java/.../mapper/` | Manual DTO ↔ entity mapping (`UserMapper`, `NoteMapper`) |
| `java/.../model/` | JPA entities (`User`, `Note`) |
| `java/.../repository/` | Spring Data JPA repositories |
| `java/.../security/` | JWT filter, security config, authentication provider |
| `java/.../service/` | Business logic (`UserService`, `NoteService`) |
| `resources/application.properties` | Default configuration (PostgreSQL, JWT) |
| `resources/application-h2.properties` | H2 in-memory profile for local development |
| `resources/static/` | Built-in web UI (`index.html`, CSS, JS) |

### `note-app/src/test/`

| Path | Responsibility |
| ---- | -------------- |
| `java/.../security/` | Unit tests for `JwtService` |
| `java/.../service/` | Unit tests for `UserService` and `NoteService` |
| `java/.../` (root) | `NotesApiIntegrationTest`, `NoteAppApplicationTests` |
| `resources/application-test.properties` | H2 test profile (`create-drop` DDL) |

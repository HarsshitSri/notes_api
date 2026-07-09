# Notes API

A RESTful backend application built with **Java 21**, **Spring Boot 4**, **Spring Data JPA (Hibernate)**, and **JWT authentication**. Users can register, log in, and manage personal notes with search, pagination, sorting, validation, and centralized exception handling.

**Repository:** [github.com/HarsshitSri/notes_api](https://github.com/HarsshitSri/notes_api)

---

## Features

* User registration and login with JWT tokens
* Create, read, update, and delete notes (scoped per authenticated user)
* Search notes by keyword
* Pagination and sorting support
* Bean Validation using `@Valid`
* Global exception handling with `@RestControllerAdvice`
* Automatic `createdAt` and `updatedAt` timestamp management
* Swagger / OpenAPI documentation
* Layered architecture (Controller → Service → Repository)

---

## Tech Stack

| Layer | Technology |
| ----- | ---------- |
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Security | Spring Security + JWT (jjwt) |
| Database | MySQL (Docker / production), H2 (local dev & tests) |
| ORM | Spring Data JPA (Hibernate) |
| API Docs | SpringDoc OpenAPI |
| Build | Maven |
| Testing | JUnit 5, Mockito, H2 |

---

## Project Structure

```text
notes_api/
├── docker-compose.yml
├── README.md
└── note-app/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/Harshit/note_app/
        │   │   ├── controller/
        │   │   ├── service/
        │   │   ├── repository/
        │   │   ├── security/
        │   │   ├── model/
        │   │   ├── dto/
        │   │   ├── exception/
        │   │   └── NoteAppApplication.java
        │   └── resources/
        │       ├── application.properties
        │       └── application-h2.properties
        └── test/
            └── resources/application-test.properties
```

---

## API Endpoints

All note endpoints require a valid JWT token in the `Authorization: Bearer <token>` header.

### Authentication (public)

| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Log in and receive a JWT token |

### Notes (authenticated)

| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| POST | `/api/notes` | Create a new note |
| GET | `/api/notes` | List notes (supports search, pagination, sorting) |
| GET | `/api/notes/{id}` | Get a note by ID |
| PUT | `/api/notes/{id}` | Update a note |
| DELETE | `/api/notes/{id}` | Delete a note |

### Pagination & sorting

```http
GET /api/notes?page=0&size=10&sortBy=updatedAt
GET /api/notes?search=spring&page=0&size=10
```

---

## Quick Start with H2 (no database setup)

The fastest way to run the project locally — uses an in-memory H2 database:

```bash
git clone git@github.com:HarsshitSri/notes_api.git
cd notes_api/note-app
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

* **API:** http://localhost:8080
* **Swagger UI:** http://localhost:8080/swagger-ui.html
* **H2 Console:** http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:notes_db`)

### Example flow

**1. Register**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","email":"demo@example.com","password":"password123"}'
```

**2. Login**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"password123"}'
```

**3. Create a note** (replace `<token>` with the JWT from login)

```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"Spring Boot","content":"Notes API is running with H2."}'
```

---

## Database Configuration

Database settings are read from environment variables. Defaults target a local MySQL instance.

| Variable | Description | Default |
| -------- | ----------- | ------- |
| `SPRING_DATASOURCE_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/notes_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `notes_user` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `notes_pass` |
| `JWT_SECRET` | JWT signing secret (min. 32 characters) | built-in dev default |
| `JWT_EXPIRATION` | Token lifetime in milliseconds | `86400000` |

For local MySQL:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/notes_db
export SPRING_DATASOURCE_USERNAME=notes_user
export SPRING_DATASOURCE_PASSWORD=notes_pass
cd note-app && mvn spring-boot:run
```

---

## Running with Docker

The project includes a `Dockerfile` and `docker-compose.yml` that start the Spring Boot API and a MySQL database together.

### Prerequisites

* [Docker](https://docs.docker.com/get-docker/)
* [Docker Compose](https://docs.docker.com/compose/)

### Quick start

From the project root:

```bash
git clone git@github.com:HarsshitSri/notes_api.git
cd notes_api
cp .env.example .env   # optional: customize credentials
docker compose up --build
```

* **Application:** http://localhost:8080
* **Swagger UI:** http://localhost:8080/swagger-ui.html

### Environment variables

| Variable | Description | Default |
| -------- | ----------- | ------- |
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `rootpassword` |
| `MYSQL_DATABASE` | Database name | `notes_db` |
| `MYSQL_USER` | Application database user | `notes_user` |
| `MYSQL_PASSWORD` | Application database password | `notes_pass` |
| `JWT_SECRET` | JWT signing secret | built-in dev default |
| `JWT_EXPIRATION` | Token lifetime in ms | `86400000` |

### Useful commands

```bash
docker compose up --build -d    # start in background
docker compose logs -f app      # view logs
docker compose down             # stop containers
docker compose down -v          # stop and remove database volume
```

---

## Running Tests

```bash
cd note-app
mvn clean test
```

Requires **Java 21**. Integration tests use an in-memory H2 database via the `test` profile.

---

## Requirements

* Java 21 (JDK)
* Maven 3.8+
* MySQL 8+ (for production / Docker setup) or H2 (for local dev / tests)

---

## Screenshots

### Create Note

![Create Note](images/create-note.png)

### Retrieve All Notes

![Retrieve All Notes](images/get-all-notes.png)

### Pagination & Sorting

![Pagination](images/pagination-and-sorting.png)

### Validation Error

![Validation Error](images/validation.png)

---

## Author

**Harsshit Sri** — [github.com/HarsshitSri](https://github.com/HarsshitSri)

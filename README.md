# Notes API

A RESTful backend application built using **Java**, **Spring Boot**, **Spring Data JPA (Hibernate)**, and **PostgreSQL**. This project provides APIs to create, retrieve, update, delete, search, and manage notes while demonstrating clean layered architecture, validation, pagination, sorting, and centralized exception handling.

---

## Features

* Create a new note
* Retrieve all notes
* Retrieve a note by ID
* Update an existing note
* Delete a note
* Search notes by keyword
* Pagination support
* Sorting support
* Bean Validation using `@Valid`
* Global Exception Handling with `@RestControllerAdvice`
* Automatic `createdAt` and `updatedAt` timestamp management
* Layered Architecture (Controller → Service → Repository)

---

## Tech Stack

* **Language:** Java
* **Framework:** Spring Boot
* **Database:** MySQL (Docker) / PostgreSQL or H2 (local development & tests)
* **ORM:** Spring Data JPA (Hibernate)
* **Build Tool:** Maven
* **Testing:** JUnit

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.harshit.note_app
│   │       ├── controller
│   │       ├── exception
│   │       ├── model
│   │       ├── repository
│   │       ├── service
│   │       └── NoteAppApplication.java
│   └── resources
│       └── application.properties
└── test
```

---

## API Endpoints

| Method | Endpoint        | Description           |
| ------ | --------------- | --------------------- |
| POST   | `/notes`        | Create a new note     |
| GET    | `/notes`        | Retrieve all notes    |
| GET    | `/notes/{id}`   | Retrieve a note by ID |
| PUT    | `/notes/{id}`   | Update a note         |
| DELETE | `/notes/{id}`   | Delete a note         |
| GET    | `/notes/search` | Search notes          |

### Pagination

```http
GET /notes?page=0&size=10
```

### Sorting

```http
GET /notes?sortBy=updatedAt
```

---

## Sample Request

```json
{
  "title": "Spring Boot",
  "content": "Complete the README for the Notes API."
}
```

## Sample Response

```json
{
  "id": 1,
  "title": "Spring Boot",
  "content": "Complete the README for the Notes API.",
  "createdAt": "2026-06-28T10:30:45",
  "updatedAt": "2026-06-28T10:30:45"
}
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

Example for local MySQL:

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/notes_db
SPRING_DATASOURCE_USERNAME=notes_user
SPRING_DATASOURCE_PASSWORD=notes_pass
```

---

## Running with Docker

The project includes a `Dockerfile` and `docker-compose.yml` that start the Spring Boot API and a MySQL database together. Database data is persisted in a Docker volume.

### Prerequisites

* [Docker](https://docs.docker.com/get-docker/)
* [Docker Compose](https://docs.docker.com/compose/)

### Quick start

From the project root:

```bash
docker compose up --build
```

The API will be available at:

* **Application:** http://localhost:8080
* **Swagger UI:** http://localhost:8080/swagger-ui.html

### Environment variables

Copy the example env file and customize credentials if needed:

```bash
cp .env.example .env
```

| Variable | Description | Default |
| -------- | ----------- | ------- |
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `rootpassword` |
| `MYSQL_DATABASE` | Database name | `notes_db` |
| `MYSQL_USER` | Application database user | `notes_user` |
| `MYSQL_PASSWORD` | Application database password | `notes_pass` |
| `JWT_SECRET` | JWT signing secret | built-in dev default |
| `JWT_EXPIRATION` | Token lifetime in ms | `86400000` |

Docker Compose passes database credentials to the application container automatically via `SPRING_DATASOURCE_*` variables.

### Useful commands

```bash
# Start in detached mode
docker compose up --build -d

# View logs
docker compose logs -f app

# Stop containers
docker compose down

# Stop containers and remove persisted database volume
docker compose down -v
```

### Docker architecture

```text
┌─────────────────────┐       ┌─────────────────────┐
│   notes-api :8080   │──────▶│   mysql :3306       │
│   (Spring Boot)     │ JDBC  │   (volume: mysql_data) │
└─────────────────────┘       └─────────────────────┘
```

---

## Running the Project (without Docker)

### Clone the repository

```bash
git clone https://github.com/<your-username>/notes-api.git
```

### Navigate to the project

```bash
cd notes-api/note-app
```

### Configure the database

Start MySQL locally (or use your own credentials) and set environment variables, or update defaults in `src/main/resources/application.properties`.

### Run the application

```bash
mvn spring-boot:run
```

or run the `NoteAppApplication` class from your IDE.

---

## Running Tests

```bash
cd note-app
mvn test
```

Integration tests use an in-memory H2 database via the `test` profile.

---

## Future Improvements

* Rate limiting
* Refresh tokens
* Email verification
* CI/CD pipeline

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

## Author

**Harshit Srivastava**


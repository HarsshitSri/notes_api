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
* **Database:** PostgreSQL
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

Update your PostgreSQL configuration in `application.properties`.

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/note_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Running the Project

### Clone the repository

```bash
git clone https://github.com/<your-username>/notes-api.git
```

### Navigate to the project

```bash
cd notes-api
```

### Configure PostgreSQL

Create a PostgreSQL database and update the credentials in `application.properties`.

### Run the application

```bash
mvn spring-boot:run
```

or run the `NoteAppApplication` class from your IDE.

---

## Future Improvements

* DTO Pattern
* JWT Authentication
* User Registration & Login
* Private Notes for Individual Users
* Swagger / OpenAPI Documentation
* Docker Support
* Unit Tests
* Integration Tests

---

## Screenshots

### Create Note

*Coming Soon*

### Retrieve All Notes

*Coming Soon*

### Search Notes

*Coming Soon*

### Pagination

*Coming Soon*

### Validation Error

*Coming Soon*

## Author

**Harshit Srivastava**


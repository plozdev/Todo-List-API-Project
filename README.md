# 📝 TaskFlow (TodoList API & SSR Dashboard)

A robust, full-stack personal task management application built with **Spring Boot 3**. This project features both a complete **RESTful API backend** (secured with JWT) and a **Server-Side Rendered (SSR) Frontend** using Thymeleaf. Users can register, log in, and perform full CRUD operations on their own tasks with strict ownership enforcement.

---

## 📸 Screenshots

*(Replace the placeholder links below with your actual image paths once you take screenshots)*

<div align="center">
  <img src="https://via.placeholder.com/800x450.png?text=Dashboard+View" alt="Dashboard View" width="80%">
  <p><i>The dynamic SSR Dashboard with Task Counters and Sorting</i></p>
</div>

<div align="center" style="display: flex; justify-content: center; gap: 20px;">
  <div style="flex: 1;">
    <img src="https://via.placeholder.com/400x300.png?text=Login+Screen" alt="Login Screen" width="100%">
  </div>
  <div style="flex: 1;">
    <img src="https://via.placeholder.com/400x300.png?text=Swagger+UI+Docs" alt="Swagger UI" width="100%">
  </div>
</div>

---

## ✨ Features

- **Full-Stack UI & API**: Use the beautiful Thymeleaf SSR dashboard directly in your browser, or consume the RESTful API via client applications.
- **User Authentication**: Secure registration and login using BCrypt password hashing. Adapted to support both stateless JWT tokens and Session-based authentication for the web UI.
- **Task CRUD & Ownership**: Create, read, update, and delete tasks. Users can only view, edit, or delete their own tasks (403 Forbidden otherwise).
- **Dynamic Stats & Pagination**: The dashboard features real-time task counters and database-level pagination/sorting via Spring Data `Pageable`. Click on table headers in the UI to sort!
- **Input Validation**: Request DTOs are validated with Jakarta Bean Validation (`@NotBlank`, `@FutureOrPresent`, etc.) with graceful error handling in the UI.
- **Automated Timestamps**: `createdAt` and `updatedAt` are managed automatically by Hibernate.
- **MapStruct Mapping**: Clean separation between entity and DTO layers.
- **Swagger UI**: Interactive API documentation for the REST endpoints.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot 3 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8 |
| Mapping | MapStruct 1.4+ |
| Validation | Jakarta Bean Validation |
| Frontend | HTML5, Vanilla CSS3 (Glassmorphism), Thymeleaf |
| Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build Tool | Maven, Lombok, Jackson |

---

## 📁 Project Structure

```
src/main/
├── java/plozdev/todolistapi/
│   ├── config/             # Spring Security, ApplicationConfig, OpenAPI config
│   ├── controllers/        # REST Controllers (API) + WebController (Thymeleaf UI)
│   ├── dto/                # Request & Response Data Transfer Objects
│   ├── entities/           # JPA Entities (User, Task, TaskPriority enum)
│   ├── exception/          # GlobalExceptionHandler
│   ├── mapper/             # MapStruct TaskMapper interface
│   ├── repository/         # Spring Data JPA Repositories
│   ├── security/           # JwtService, JwtAuthFilter
│   └── services/           # Service interfaces and implementations
└── resources/
    ├── static/css/         # Vanilla CSS stylesheets (auth.css, dashboard.css)
    ├── templates/          # Thymeleaf HTML views (login, register, dashboard)
    └── application.yaml    # Application and Database configuration
```

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/plozdev/Todo-List-API-Project.git
cd Todo-List-API-Project
```

### 2. Configure the database

Create a MySQL database:

```sql
CREATE DATABASE todo_list_db;
```

Then update `src/main/resources/application.yaml` with your credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/todo_list_db?createDatabaseIfNotExist=true
    username: YOUR_MYSQL_USERNAME
    password: YOUR_MYSQL_PASSWORD
  jpa:
    hibernate:
      ddl-auto: update   # Automatically creates/updates tables on startup
```

### 3. Build and Run

```bash
mvn spring-boot:run
```

- **Web Dashboard**: Open `http://localhost:8080/`
- **Swagger UI (API Docs)**: Open `http://localhost:8080/swagger-ui/index.html`

---

## 📌 API Endpoints (For REST Clients)

If you wish to build a separate frontend (e.g., React.js), you can use the REST APIs directly:

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|:---:|
| `POST` | `/api/v1/auth/register` | Register a new user | ❌ |
| `POST` | `/api/v1/auth/login` | Login and get JWT token | ❌ |

### Tasks

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|:---:|
| `GET` | `/api/v1/tasks` | Get all tasks (paginated) | ✅ |
| `GET` | `/api/v1/tasks/{id}` | Get a specific task | ✅ |
| `POST` | `/api/v1/tasks` | Create a new task | ✅ |
| `PUT` | `/api/v1/tasks/{id}` | Update a task | ✅ |
| `DELETE` | `/api/v1/tasks/{id}` | Delete a task | ✅ |

*(Note: API tasks require setting the `Authorization: Bearer <token>` header)*

---

## 🗄️ Database Schema

The application uses `ddl-auto: update`, so tables are created automatically on startup.

### `users` table

| Column | Type | Notes |
|--------|------|-------|
| `id` | INT (PK, AUTO_INCREMENT) | |
| `email` | VARCHAR(100) | Unique |
| `password_hash` | VARCHAR | BCrypt hashed |
| `name` | VARCHAR(255) | |
| `created_at` | TIMESTAMP | |

### `tasks` table

| Column | Type | Notes |
|--------|------|-------|
| `id` | INT (PK, AUTO_INCREMENT) | |
| `user_id` | INT (FK → users.id) | |
| `title` | VARCHAR | NOT NULL |
| `description` | TEXT | Nullable |
| `is_completed` | BOOLEAN | Default: false |
| `priority` | ENUM(`LOW`, `MEDIUM`, `HIGH`) | |
| `due_date` | DATETIME | |
| `created_at` | DATETIME | Auto-managed |
| `updated_at` | DATETIME | Auto-managed |

---

## 📜 License & Credits

This project is created for educational and portfolio purposes, showcasing profound understanding of the Spring Ecosystem and modern Web UI integration.

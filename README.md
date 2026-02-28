# Task Manager — Backend Service

A production-ready, secure backend service for managing tasks with a complete **approval workflow**, built using **Java 17** and **Spring Boot 3.x**.

> **VIVATECH R&D** — Backend Engineering Assessment

---

## Author

**Aman Sahu**



## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Core language |
| Spring Boot 3.x | Application framework |
| Spring Security | Authentication & Authorization |
| JWT (JJWT 0.11.5) | Stateless token management |
| JPA / Hibernate | ORM & database interaction |
| MySQL 8 | Relational database |
| Maven | Build tool |
| Lombok | Boilerplate reduction |

---

## What This Project Does

Task Manager is a role-based REST API backend where:

- **Users** can register, log in, create tasks, and view their own tasks
- **Admins** can view all tasks, approve or reject them, and access analytics
- **Authentication** is handled via JWT access tokens (15 min) and refresh tokens (7 days)
- **Security** is fully stateless — no sessions, no cookies

The system enforces strict business rules at the service and security layer, ensuring users can only do what their role permits.

---

## Project Structure

```
src/main/java/com/vivatech/taskmanager/
│
├── config/          # App beans, security filter chain, role-based access rules
├── controller/      # REST controllers for Auth, Tasks, and Analytics
├── dto/             # Request and response data transfer objects
├── entity/          # JPA entities: User, Task, RefreshToken
├── enums/           # Role (USER, ADMIN) and TaskStatus (CREATED, APPROVED, REJECTED)
├── exception/       # Custom exception, error codes, global exception handler
├── repository/      # Spring Data JPA repositories
├── security/        # JWT generation, validation, request filter, user detail loader
└── service/         # Business logic interfaces and implementations
```

---

## API Overview

### Auth APIs — `/auth`

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/auth/register` | Public | Register a new user (USER or ADMIN role) |
| POST | `/auth/login` | Public | Login and receive access + refresh tokens |
| POST | `/auth/logout` | Authenticated | Logout and invalidate the refresh token |
| POST | `/auth/refresh` | Public | Get a new access token using refresh token |

---

### Task APIs — `/tasks`

> All task endpoints require `Authorization: Bearer <accessToken>` header

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/tasks` | USER only | Create a new task |
| GET | `/tasks` | USER + ADMIN | Fetch tasks (USER sees own; ADMIN sees all) |
| PUT | `/tasks/{id}/approve` | ADMIN only | Approve a task |
| PUT | `/tasks/{id}/reject` | ADMIN only | Reject a task |

---

### Analytics APIs — `/analytics`

> Admin access only

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/analytics/tasks-by-status` | Count of tasks grouped by status (CREATED, APPROVED, REJECTED) |
| GET | `/analytics/daily-task-count` | Count of tasks created per day |

---

## Business Rules

| Rule | How It's Enforced |
|------|-------------------|
| Only USERs can create tasks | `@PreAuthorize("hasRole('USER')")` |
| USERs can only view their own tasks | Role check in `TaskServiceImpl` |
| ADMINs can view all tasks | Role check in `TaskServiceImpl` |
| ADMINs can approve/reject tasks | `@PreAuthorize("hasRole('ADMIN')")` |
| ADMINs cannot create tasks | Spring Security blocks POST `/tasks` for ADMINs |
| Approved tasks cannot be rejected | Status check in `TaskServiceImpl` |
| Rejected tasks cannot be approved | Status check in `TaskServiceImpl` |

---

## Security Design

Every request goes through a `JwtAuthFilter` which:
1. Reads the `Authorization` header
2. Validates the JWT token
3. Extracts the username and role
4. Sets authentication in the `SecurityContext`
5. Allows Spring Security to enforce role-based access before reaching the controller

- Passwords are encrypted with **BCrypt**
- Sessions are fully **stateless**
- Role enforcement is done at the **security layer**, not controller logic

---

## Token Details

| Token | Expiry | Purpose |
|-------|--------|---------|
| Access Token | 15 minutes | Authenticate API requests |
| Refresh Token | 7 days | Generate a new access token without re-login |

---

## Error Handling

All errors return a consistent JSON response structure. Errors are managed centrally via a `GlobalExceptionHandler` using custom `AppException` and `ErrorCode` classes.

| Scenario | HTTP Status |
|----------|-------------|
| Username already exists | 409 Conflict |
| Email already registered | 409 Conflict |
| Invalid credentials | 401 Unauthorized |
| Token expired | 401 Unauthorized |
| Task not found | 404 Not Found |
| Task already approved/rejected | 400 Bad Request |
| Unauthorized action | 403 Forbidden |
| Validation error | 400 Bad Request |

---

## Prerequisites

- Java 17+
- MySQL 8+
- Maven 3.8+

---

## Getting Started

1. **Clone the repository**
2. **Set up the MySQL database** — create a database named `vivatech` and run the SQL scripts located in `src/main/resources/db/`
3. **Configure environment** — copy `.env.example` to `.env` and fill in your DB credentials and JWT secret
4. **Run the application** using the Maven wrapper: `./mvnw spring-boot:run`

Server starts at: **`http://localhost:8081`**

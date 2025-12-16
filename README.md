### 🚧 Major Update In Progress: Gather V2
I am currently refactoring this project into a **Social Group Planner** with real-time chat and expense splitting.

👉 **[Click here to see the active development branch (v2-social-upgrade)](https://github.com/AGhasr/event-registration/tree/v2-social-upgrade)**

# 🎉 Event Registration System

A Spring Boot application for managing events with **both web (Thymeleaf)** and **REST API** access, now secured using **JWT authentication**.

## 🌟 **[Live Demo](https://event-registration-app-orzo.onrender.com)**

**Default login credentials:**
- Username: `admin`
- Password: `admin`

---

## 📌 Features

- Public list of upcoming events
- User registration with encrypted passwords
- **JWT-based authentication** for REST API access
- **REST API endpoints** for authentication, event registration, and listing events
- User login with role-based access (User/Admin)
- Admin can create and delete events
- Users can register or cancel event participation
- Prevents duplicate registrations
- Thymeleaf templates for the web interface (role-based UI rendering)
- H2 in-memory database for quick setup

---

## 🚀 Technologies Used

- Java 17
- Spring Boot 3
- Spring Security (JWT authentication & role-based access control)
- Spring Data JPA (H2 database)
- Thymeleaf (template engine)
- Bootstrap 5 (basic responsive design)
- Maven
- Docker

---

## 🎯 How to Run the Application

### 🐳 Quick Start with Docker (Recommended)

You can run the application immediately with Docker without cloning or building anything:

#### 1. Generate a secure JWT secret:

**Linux/macOS:**
```bash
openssl rand -base64 64
```

**Or using Node.js:**
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

#### 2. Run with Docker (replace `YOUR_GENERATED_SECRET` with the secret from step 1):

```bash
docker run -e JWT_SECRET="YOUR_GENERATED_SECRET" -p 8080:8080 aghasr/event-registration-app:latest
```

⚠️ **Do not use short strings like `1234`. JWT requires at least a 256-bit key for HS256 signing.**

---

### 🔧 Local Development (Optional)

If you want to modify the code:

#### 1. Clone the repository
```bash
git clone https://github.com/AGhasr/event-registration.git
cd event-registration-app
```

#### 2. Create a `.env` file with your JWT secret
```bash
echo "JWT_SECRET=$(openssl rand -base64 64)" > .env
```

#### 3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Access the application

```
http://localhost:8080
```

### 4. Test the REST API (JWT required)

#### Register:
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "ali",
  "password": "1234"
}
```

#### Login (get JWT token):
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "ali",
  "password": "1234"
}
```

Response will contain the token — use it in the Authorization header for API requests:
```
Authorization: Bearer <token>
```

## 👥 Default Web Users

| Username | Password | Role  |
|----------|----------|-------|
| admin    | admin    | ADMIN |

## 🗂 Project Structure

```
src/main/java/org/example/eventregistration
├── config       # Security config, JWT filter, data loader
├── controller   # Web controllers & REST API controllers
├── dto          # Data Transfer Objects (EventDTO, Auth DTOs)
├── model        # Entity classes (User, Event)
├── repository   # Spring Data JPA repositories
├── security     # JWT-related classes (JwtService, JwtAuthFilter)
└── service      # Business logic (CustomUserDetailsService)
```

Templates for the web UI are in:
```
src/main/resources/templates
```
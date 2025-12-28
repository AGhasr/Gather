# Gather - Social Group Planner

Gather is a Spring Boot application designed to help friends organize trips and events together. Originally built as a simple event registration system, it has been refactored into a social planning platform with real-time communication and shared expense tracking.

The application functions via a **server-side rendered Thymeleaf UI** and is containerized for production.

## 🔗 Live Demo
**Access the app here:** [https://gather-xrz0.onrender.com](https://gather-xrz0.onrender.com)
> *Note: Hosted on Render's free tier. The server may take 60 seconds to "wake up" if it has been inactive.*

## 🚀 Key Features (v2)

* **Group Management:** Create squads (groups) and invite friends via username.
* **Event Scheduling:** Create events specific to a group, track attendance, and archive past events.
* **Real-Time Chat:** WebSocket-based group chat with persistent history.
* **Shared Wallet:** Log shared expenses within a group and track total spending.
* **Role-Based Access:** Admins manage event deletions; Users manage registrations and expenses.
* **Notifications:** Email alerts when users are added to groups.

## ⚠️ Project Status: Refactoring

This project is currently migrating from **v1 (Event Registration)** to **v2 (Gather)**.
* ✅ **Web UI (Thymeleaf):** Fully updated with Groups, Chat, and Expenses.
* 🚧 **REST API:** Currently reflects v1 logic. Endpoints for Groups, Chat, and Expenses are **pending implementation**.

## 🛠️ Tech Stack

* **Core:** Java 17, Spring Boot 3
* **Data:** Spring Data JPA, PostgreSQL (Production), H2 (Local Dev)
* **Security:** Spring Security, BCrypt, JWT
* **Real-time:** Spring WebSockets (STOMP), SockJS
* **Frontend:** Thymeleaf, Bootstrap 5
* **DevOps:** Docker, Render

## 📦 Getting Started

### Prerequisites
* Java 17+
* Maven
* Docker (Optional, for containerized run)

### Run Locally (Simple)

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/AGhasr/gather.git](https://github.com/AGhasr/gather.git)
    cd gather
    ```

2.  **Build and Run**
    The app uses H2 by default for local development, so no database setup is required.
    ```bash
    mvn spring-boot:run
    ```

3.  **Access the App**
    * Web UI: `http://localhost:8080`
    * H2 Console: `http://localhost:8080/h2-console`

### 🔑 Environment Variables
To run the app in production (or via Docker) with full functionality, set these variables.

| Variable | Description | Default (if unset) |
| :--- | :--- | :--- |
| `JWT_SECRET` | Secret key for generating tokens | *(Hardcoded dev key)* |
| `SPRING_DATASOURCE_URL` | Database connection URL | `jdbc:h2:mem:testdb` |
| `SPRING_DATASOURCE_USERNAME` | Database User | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Database Password | `password` |
| `MAIL_USERNAME` | SMTP Username (e.g., Mailtrap) | `null` |
| `MAIL_PASSWORD` | SMTP Password | `null` |

## 🔐 Default Credentials

The application seeds default data on startup (see `DataLoader.java`).

| Role | Username | Password |
| :--- | :--- | :--- |
| **Admin** | `admin` | `admin` |
| **User** | `alice` | `1234` |
| **User** | `bob` | `1234` |

## 📡 API Reference (Legacy v1)

> **Note:** The REST API supports the legacy Event Registration flow. It does not yet support Groups or Chat.

**Base URL:** `/api`

* `POST /auth/login` - Returns JWT Token
* `GET /events` - List public events
* `POST /events/register/{id}` - Register for an event

## 🔮 Roadmap

* [ ] Update REST API to support Group and Expense endpoints.
* [ ] "Who owes who" algorithm for expense settlement.
* [ ] Cloudinary/S3 integration for profile pictures.
* [ ] Typing indicators for group chat.
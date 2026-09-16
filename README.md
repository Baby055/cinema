# POJA Cinema Management API

This repository contains the backend API for a Cinema Reservation Management System, built as the **PROG4 Final Exam**. It is generated from the **POJA starter template** and leverages Java with Spring Boot to provide a scalable, robust, and clean architecture.

## 🚀 Project Overview

The **POJA Cinema** API automates the core operations of a movie theater. It provides secure, automated endpoints to manage users, movies, projections, rooms, seats, and reservations.

### Key Features
* **Catalog Management:** Manage movies (with genres) and schedule projections across the theater's rooms and seats.
* **Reservation System:** Book seats for a projection over secure REST endpoints, with full seat-availability validation.
* **Role-Based Access Control:** Three user roles — `CLIENT`, `EMPLOYEE`, and `MANAGER` — enforced by HTTP Basic authentication with BCrypt-hashed passwords.
* **Business Rules Automation:** Backend logic enforces rule-of-reservation statuses (PENDING, SUCCESS, CANCELED), permission boundaries, and seat-conflict detection.

## 🛠️ Tech Stack & Architecture

* **Framework:** Spring Boot (Java 17+, this project targets **Java 21**)
* **Template Engine:** POJA (Production-Ready Architecture)
* **Build Tool:** Gradle 8.x
* **Database:** PostgreSQL (managed via **Flyway** migrations, schema validated on startup)
* **Deployment:** AWS Lambda via SAM (serverless), with S3, SES, and event-driven support
* **Code Quality:** Google Java Format configured via `format.sh` / `format.bat` and enforced in CI
* **API Specification:** OpenAPI 3.0 (`doc/api.yml`)

## 📁 Repository Structure

```text
├── .github/workflows/    # CI/CD deployment pipelines
├── .shell/               # Automation and deployment shell scripts
├── doc/                  # OpenAPI specification (api.yml)
├── src/                  # Main Java and Spring Boot source files
├── load-test/            # Artillery load-testing scenarios
├── build.gradle          # Dependency definitions and Gradle tasks
└── README.md             # Project documentation
```

## 🧑 Role-Based Access

| Role | Description |
|------|-------------|
| `CLIENT` | Can browse movies/projections and manage their own reservations and profile. |
| `EMPLOYEE` | Can view all reservations and update their status. |
| `MANAGER` | Full access: manage movies, projections, reservations, and all users. |

## ⚙️ Local Setup & Installation

### Prerequisites
* Java JDK 21 or higher
* Gradle 8.x
* A running PostgreSQL instance (or a free Neon Postgres branch)

### 1. Clone the Repository
```bash
git clone https://github.com/Baby055/cinema.git
cd cinema
```

### 2. Configure Environment Variables
Create a local configuration file or set your system environment variables for your database connection:
```bash
export DATASOURCE_URL=jdbc:postgresql://localhost:5432/your_db_name?sslmode=require
export DATASOURCE_USERNAME=your_username
export DATASOURCE_PASSWORD=your_password
```

### 3. Code Formatting
Before committing, ensure your code complies with the project's formatting rules:
```bash
# On Linux/macOS
./format.sh

# On Windows
format.bat
```

### 4. Build and Run the Application
```bash
./gradlew bootRun
```
The server will start locally on port `8080` (overridable via `SERVER_PORT`).

## 🧪 Testing

To run the complete automated test suite (40+ tests covering services and authorization, with Testcontainers and MockMvc):
```bash
./gradlew test
```
JaCoCo coverage reports are generated under `build/reports/jacoco/`.

## 📡 Main Endpoints

| Method | Path | Access |
|--------|------|--------|
| GET | `/movies` | Public |
| PUT | `/movies` | Manager |
| GET | `/projections` | Public |
| PUT | `/projection` | Manager |
| GET | `/reservations` | Employee, Manager |
| PUT | `/reservation` | Authenticated |
| GET | `/users` | Manager |
| PUT | `/users` | Public (create) / Authenticated (update) |

Health checks (`/ping`, `/health/db`, `/health/bucket`, `/health/email`) are also exposed for monitoring.

---
*Developed by RANAIVOMANANA Sombin'ny Aina as part of the HEI Madagascar Computer Science curriculum.*
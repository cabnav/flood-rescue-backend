## Flood Rescue Coordination Backend

This repository contains the backend for the Flood Rescue Coordination System, built with **Spring Boot 3**, **JWT-based authentication**, and **PostgreSQL** as the primary database.

---

## 1. Database Architecture (Neon Postgres – No Local DB Required)

The project **migrated from SQL Server to PostgreSQL (Neon Cloud)** to:

- **Support team members with weaker hardware** (especially in the North/Bắc).
- **Provide a single shared database instance** for the entire team, so everyone develops and tests against the same data.
- **Eliminate the need for any local database installation.**

All database connection details are already configured in `application.properties` and point to a Neon-hosted Postgres instance. As long as you can run the Spring Boot application with internet access, **you do NOT need to install SQL Server or Postgres locally.**

---

## 2. API Tools: Swagger & Postman

The backend exposes a **live, interactive API documentation** via **Swagger UI**:

- **Swagger UI URL:** `http://localhost:8080/swagger-ui/index.html`
- This is the **official Live API Doc** for both Backend and Frontend teams.
- You can:
  - Explore all endpoints (`/api/v1/...`)
  - See request/response models
  - Authorize with JWT tokens and test protected endpoints directly.

For deeper or automated testing, you can also use **Postman**:

- Import collections as needed.
- Use the same base URL and JWT tokens shown in Swagger.
- Recommended for chaining requests, regression testing, and debugging complex flows.

**Rule of thumb:**  
- **Swagger** = documentation + quick manual tests.  
- **Postman** = advanced testing and workflows.

---

## 3. Getting Started (Developer Setup)

### Prerequisites

- **JDK 17** (or JDK 21, as long as your IntelliJ is configured correctly)
- **IntelliJ IDEA** (recommended) or another Java IDE with Spring support
- Git installed and configured

### Steps

1. **Clone the repository**

   ```bash
   git clone <REPO_URL>
   cd flood-rescue-backend
   ```

2. **Checkout the Sprint 1 branch**

   ```bash
   git checkout feature/sprint1-core-complete
   ```

3. **Open in IntelliJ IDEA**

   - Open the project as a **Maven** project.
   - Ensure the **Project SDK** is set to **JDK 17** (or 21 if your environment is configured for it).

4. **Run the application**

   - Locate the `BackendApplication` class.
   - Run it as a **Spring Boot Application**.
   - The server will start on: `http://localhost:8080`

5. **Verify Swagger UI**

   - Open a browser and go to:  
     `http://localhost:8080/swagger-ui/index.html`
   - You should see the Flood Rescue API documentation and be able to try endpoints.

---

## 4. Sample Accounts (Seeded by `DataSeeder`)

The project includes a `DataSeeder` (`com.floodrescue.backend.common.config.DataSeeder`) that seeds **roles, users, SOS requests, vehicles, and warehouses** on startup (idempotently).

### User Roles & Sample Accounts

The following accounts are created automatically if they do not already exist:

- **Admin**
  - **Email:** `admin@floodrescue.com`
  - **Password:** `admin123`
  - **Role:** `ADMIN`
  - **Usage:** Full system administration, user management, approvals.

- **Citizen**
  - **Email:** `citizen@test.com`
  - **Password:** `citizen123`
  - **Role:** `CITIZEN`
  - **Usage:** Creates SOS rescue/relief requests from the field.

- **Rescue Team**
  - **Email:** `team@rescue.com`
  - **Password:** `team123`
  - **Role:** `RESCUE_TEAM`
  - **Usage:** Used for mission assignments and rescue operations.

All seeded users are set with `isActive = true` so they can log in immediately.

### Seeded SOS Requests & Master Data

For convenience during development:

- **SOS Requests**: 3 example requests linked to `citizen@test.com` with different statuses and priorities (`CREATED`, `IN_PROGRESS`, `CRITICAL`, `HIGH`, `NORMAL`).
- **Vehicles**: A small fleet (e.g. Boat, Truck, Ambulance) with different statuses (`AVAILABLE`, `IN_USE`, `MAINTENANCE`).
- **Warehouses**: A few sample warehouses with resource and supply IDs to support inventory-related UI.

This allows the Frontend team to see meaningful data immediately after the first run, without manual setup.

---

## 5. API Integration Guide for Frontend

For the **Frontend team**, there is a dedicated, detailed API contract document:

- **File:** `API_INTEGRATION_GUIDE.md`
- **Location:** Project root

This guide includes:

- Base URL and environment variable setup (`VITE_REACT_APP_API_BASE_URL`).
- Full list of endpoints (`Auth`, `Requests`, `Missions`, `Vehicles`, `Warehouses`, `Admin`).
- TypeScript interfaces for all request/response DTOs.
- JWT authentication & Authorization header format.
- Role-based access rules and error-handling patterns (`ApiResponse<T>` envelope).

**Frontend developers should read this file first** before integrating any endpoint. It is kept in sync with the current codebase and the live Swagger documentation.

---

## 6. Conventions & Notes

- **Response Wrapper:** All APIs return a common envelope:

  ```json
  {
    "success": true | false,
    "message": "Human readable message",
    "data": { ... }
  }
  ```

- **Authentication:**
  - JWT Bearer Token in `Authorization: Bearer <token>` header.
  - Swagger UI has an **Authorize** button to input the token and test protected endpoints.

- **Branching:**
  - `feature/sprint1-core-complete` is the main working branch for Sprint 1 core features.

If you are unsure how to use any endpoint, first check:

1. `API_INTEGRATION_GUIDE.md`
2. Swagger UI (`/swagger-ui/index.html`)
3. Then ask the backend owner or lead for clarification.


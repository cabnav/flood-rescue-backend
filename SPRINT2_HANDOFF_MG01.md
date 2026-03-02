# Sprint 2 Handoff Document - MG-01: Vehicle Management

**Task ID:** MG-01 - Quản lý Phương tiện (Vehicle Management)  
**Status:** ✅ **Backend 100% Complete**  
**Sprint:** Sprint 2  
**Date:** January 2026

---

## 1. Task Summary

**MG-01** provides a complete CRUD API for managing rescue vehicles (phương tiện cứu hộ) in the Flood Rescue system. This feature allows Managers and Admins to:

- Create new vehicles in the fleet
- View all vehicles or filter by status
- Update vehicle information (type, model, license plate, capacity)
- Update vehicle status (AVAILABLE, IN_USE, MAINTENANCE)
- Delete vehicles (when not in use)
- Track vehicle availability for mission assignment

**All endpoints are secured** and require `MANAGER` or `ADMIN` role access.

---

## 2. Key Features & Business Logic

### 2.1. VehicleStatus Enum (Trạng thái phương tiện)

Every vehicle must have **exactly one status** at any given time. The system uses a strict enum with **3 allowed values**:

```java
public enum VehicleStatus {
    AVAILABLE,      // Sẵn sàng cho nhiệm vụ mới
    IN_USE,         // Đang được sử dụng trong nhiệm vụ
    MAINTENANCE     // Đang bảo trì/sửa chữa
}
```

**Important:**
- Status is stored as `@Enumerated(EnumType.STRING)` in the database
- Status field is **required** (`nullable = false`) when creating/updating vehicles
- Invalid status values will result in `400 Bad Request` errors

### 2.2. "Chặn dữ liệu" Logic - IN_USE Protection

**Critical Business Rule:** When a vehicle's status is `IN_USE`, it is **strictly protected** from modification or deletion to prevent data inconsistency during active missions.

#### Protected Operations:

1. **`PUT /api/v1/vehicles/{id}` (Update Core Info)**
   - **Blocked if:** `vehicle.status == IN_USE`
   - **Error Response:** `400 Bad Request`
   - **Message:** `"Vehicle is currently IN_USE and cannot be modified"`
   - **What's blocked:** Updates to `type`, `model`, `licensePlate`, `capacityPerson`, or `status` via this endpoint

2. **`DELETE /api/v1/vehicles/{id}` (Delete Vehicle)**
   - **Blocked if:** `vehicle.status == IN_USE`
   - **Error Response:** `400 Bad Request`
   - **Message:** `"Vehicle is currently IN_USE and cannot be deleted"`

#### Allowed Operations (Even when IN_USE):

- **`PUT /api/v1/vehicles/{id}/status`** - Status-only updates are **always allowed**
  - This allows changing status from `IN_USE` → `AVAILABLE` (mission complete) or `IN_USE` → `MAINTENANCE` (needs repair)
  - This is the **only way** to free up a vehicle that's currently `IN_USE`

**Implementation Location:**
- Logic enforced in `VehicleServiceImpl.updateVehicle()` (line 55-57)
- Logic enforced in `VehicleServiceImpl.deleteVehicle()` (line 75-77)

### 2.3. Role-Based Access Control

**All endpoints in `VehicleController` are protected** with:

```java
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
```

**Required Authentication:**
- Frontend must include JWT Bearer Token in the `Authorization` header:
  ```
  Authorization: Bearer <JWT_TOKEN>
  ```
- User must have role `MANAGER` or `ADMIN` (as returned in the login response)
- Unauthorized requests will receive `403 Forbidden`

---

## 3. API Guide for Frontend

### 3.1. Endpoint List

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/v1/vehicles` | POST | Create a new vehicle | MANAGER/ADMIN |
| `/api/v1/vehicles/{id}` | GET | Get vehicle details by ID | MANAGER/ADMIN |
| `/api/v1/vehicles` | GET | Get all vehicles | MANAGER/ADMIN |
| `/api/v1/vehicles/{id}` | PUT | Update vehicle core info | MANAGER/ADMIN |
| `/api/v1/vehicles/{id}` | DELETE | Delete a vehicle | MANAGER/ADMIN |
| `/api/v1/vehicles/{id}/status` | PUT | Update vehicle status only | MANAGER/ADMIN |
| `/api/v1/vehicles/status/{status}` | GET | Filter vehicles by status | MANAGER/ADMIN |

**Base URL:** `http://localhost:8080/api/v1/vehicles`

### 3.2. Sample Request Bodies

#### Create Vehicle (POST /api/v1/vehicles)

```json
{
  "depotId": null,
  "type": "Boat",
  "model": "Rescue Boat 3000",
  "licensePlate": "VR-001",
  "capacityPerson": 8,
  "status": "AVAILABLE"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Vehicle created successfully",
  "data": {
    "vehicleId": 1,
    "depotId": null,
    "type": "Boat",
    "model": "Rescue Boat 3000",
    "licensePlate": "VR-001",
    "capacityPerson": 8,
    "status": "AVAILABLE"
  }
}
```

#### Update Status Only (PUT /api/v1/vehicles/{id}/status)

```json
{
  "status": "IN_USE"
}
```

**Valid status values:** `"AVAILABLE"`, `"IN_USE"`, `"MAINTENANCE"` (case-sensitive)

### 3.3. Filter by Status Endpoint

**Endpoint:** `GET /api/v1/vehicles/status/{status}`

**Example:**
- `GET /api/v1/vehicles/status/AVAILABLE` → Returns all available vehicles
- `GET /api/v1/vehicles/status/IN_USE` → Returns all vehicles currently in use
- `GET /api/v1/vehicles/status/MAINTENANCE` → Returns all vehicles under maintenance

**Use Case:** This endpoint is essential for the **Mission Assignment** module to quickly find available vehicles when assigning rescue missions.

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "vehicleId": 1,
      "depotId": null,
      "type": "Boat",
      "model": "Rescue Boat 3000",
      "licensePlate": "VR-001",
      "capacityPerson": 8,
      "status": "AVAILABLE"
    },
    {
      "vehicleId": 2,
      "depotId": null,
      "type": "Truck",
      "model": "Cargo Truck X1",
      "licensePlate": "VT-002",
      "capacityPerson": 3,
      "status": "AVAILABLE"
    }
  ]
}
```

### 3.4. Error Handling

**400 Bad Request - IN_USE Protection:**
```json
{
  "success": false,
  "message": "Vehicle is currently IN_USE and cannot be modified",
  "data": null
}
```

**400 Bad Request - Invalid Status:**
```json
{
  "success": false,
  "message": "Invalid vehicle status: INVALID_STATUS",
  "data": null
}
```

**404 Not Found:**
```json
{
  "success": false,
  "message": "Vehicle not found with id: 999",
  "data": null
}
```

**403 Forbidden (Unauthorized Role):**
- User does not have `MANAGER` or `ADMIN` role
- Missing or invalid JWT token

---

## 4. Important Infrastructure Fixes (For Whole Team)

### 4.1. Neon PostgreSQL Connection Timeout Configuration

**Problem:** Neon Cloud uses a **serverless architecture** that puts databases to "sleep" after periods of inactivity. When the database wakes up (cold start), it can take **10-30+ seconds**, causing `SocketTimeoutException: Read timed out` errors during application startup.

**Solution Applied in `application.properties`:**

```properties
# JDBC URL with explicit timeouts
spring.datasource.url=jdbc:postgresql://ep-aged-sunset-a1n5w3bm-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&connectTimeout=60&socketTimeout=60

# HikariCP Hardened Settings
spring.datasource.hikari.connection-timeout=90000          # 90 seconds wait time
spring.datasource.hikari.maximum-pool-size=5              # Safe for Neon Free tier
spring.datasource.hikari.keepalive-time=30000             # Keep connections alive
spring.datasource.hikari.validation-timeout=5000          # Connection validation timeout
spring.datasource.hikari.initialization-fail-timeout=0    # CRITICAL: Don't fail on first attempt
```

**Key Settings Explained:**

- **`connection-timeout=90000`** (90 seconds):
  - HikariCP connection pool will wait up to 90 seconds for a connection
  - Gives Neon enough time to wake up from sleep mode

- **`initialization-fail-timeout=0`** ⚠️ **CRITICAL:**
  - **Prevents application crash** if the first connection attempt fails
  - HikariCP will retry continuously in the background instead of failing immediately
  - **This ensures the server starts even if the DB is still waking up**
  - Without this, the entire Spring Boot application would fail to start

- **`connectTimeout=60&socketTimeout=60`** (in JDBC URL):
  - PostgreSQL driver-level timeouts
  - Ensures the driver itself waits long enough for Neon's cold start

**Impact:**
- ✅ Application now starts successfully even when Neon is in sleep mode
- ✅ No more `ERR_CONNECTION_REFUSED` on Swagger UI due to failed startup
- ✅ Database connections are established automatically once Neon wakes up

### 4.2. JPA Schema Management

**Current Setting:**
```properties
spring.jpa.hibernate.ddl-auto=none
```

**Note:** Schema auto-update is temporarily disabled to speed up startup. When you need to create/update tables:
1. Change to `spring.jpa.hibernate.ddl-auto=update` temporarily
2. Run the application to apply schema changes
3. Change back to `none` for normal operation

---

## 5. Test Accounts (From DataSeeder)

The following test accounts are automatically created when the application starts (if they don't already exist):

### Admin Account
- **Email:** `admin@floodrescue.com`
- **Password:** `admin123`
- **Role:** `ADMIN`
- **Status:** Active (`isActive = true`)
- **Use Case:** Full system administration, can access all Vehicle Management endpoints

### Citizen Account
- **Email:** `citizen@test.com`
- **Password:** `citizen123`
- **Role:** `CITIZEN`
- **Status:** Active (`isActive = true`)
- **Use Case:** Creates SOS requests (cannot access Vehicle Management APIs)

### Rescue Team Account
- **Email:** `team@rescue.com`
- **Password:** `team123`
- **Role:** `RESCUE_TEAM`
- **Status:** Active (`isActive = true`)
- **Use Case:** Mission operations (cannot access Vehicle Management APIs)

**Note:** To test Vehicle Management endpoints, you **must** login with the **Admin** account (`admin@floodrescue.com` / `admin123`).

---

## 6. Integration Notes for Frontend Team

### 6.1. Workflow Recommendation

1. **Login as Admin/Manager:**
   ```
   POST /api/v1/auth/login
   Body: { "email": "admin@floodrescue.com", "password": "admin123" }
   ```
   Save the `token` from the response.

2. **Create Vehicle:**
   ```
   POST /api/v1/vehicles
   Headers: { "Authorization": "Bearer <token>" }
   Body: { ...VehicleRequest }
   ```

3. **Check Available Vehicles (Before Mission Assignment):**
   ```
   GET /api/v1/vehicles/status/AVAILABLE
   Headers: { "Authorization": "Bearer <token>" }
   ```

4. **Update Status When Assigning to Mission:**
   ```
   PUT /api/v1/vehicles/{id}/status
   Headers: { "Authorization": "Bearer <token>" }
   Body: { "status": "IN_USE" }
   ```

5. **Update Status When Mission Completes:**
   ```
   PUT /api/v1/vehicles/{id}/status
   Headers: { "Authorization": "Bearer <token>" }
   Body: { "status": "AVAILABLE" }  // or "MAINTENANCE" if vehicle needs repair
   ```

### 6.2. UI/UX Recommendations

- **When showing vehicle list:**
  - Display status badges with color coding:
    - 🟢 `AVAILABLE` (green)
    - 🔴 `IN_USE` (red)
    - 🟡 `MAINTENANCE` (yellow)

- **When user tries to edit/delete an `IN_USE` vehicle:**
  - Show a clear error message:
    ```
    "Phương tiện đang được sử dụng trong nhiệm vụ, không thể chỉnh sửa/xoá. 
    Vui lòng kết thúc nhiệm vụ hoặc đổi trạng thái trước."
    ```
  - Disable edit/delete buttons for `IN_USE` vehicles in the UI

- **Status filter dropdown:**
  - Provide quick filters: "All", "Available", "In Use", "Maintenance"
  - Use the `GET /api/v1/vehicles/status/{status}` endpoint for filtering

---

## 7. Related Documentation

- **Detailed Feature Guide:** `DOC_MG01_VEHICLE_MANAGEMENT.md`
- **API Integration Guide:** `API_INTEGRATION_GUIDE.md`
- **Live API Documentation:** `http://localhost:8080/swagger-ui/index.html`

---

## 8. Next Steps

**For Backend Team:**
- ✅ MG-01 Backend is **100% complete**
- Ready for Frontend integration
- Ready for Mission Assignment module integration (to mark vehicles as `IN_USE`)

**For Frontend Team:**
- Start implementing Vehicle Management UI
- Use Swagger UI for testing endpoints
- Reference `API_INTEGRATION_GUIDE.md` for complete API contract

**For Mission Module (Future Sprint):**
- When assigning a vehicle to a mission, call:
  ```
  PUT /api/v1/vehicles/{vehicleId}/status
  Body: { "status": "IN_USE" }
  ```
- When mission completes, call:
  ```
  PUT /api/v1/vehicles/{vehicleId}/status
  Body: { "status": "AVAILABLE" }  // or "MAINTENANCE"
  ```

---

**Document Version:** 1.0  
**Last Updated:** January 2026  
**Prepared By:** Backend Development Team

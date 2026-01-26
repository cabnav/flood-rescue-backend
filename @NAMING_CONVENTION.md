# Flood Rescue Coordination and Relief Management System

## Naming Convention Document

**Project Code:** SP26SWP01  
**Version:** 1.0  
**Last Updated:** January 25, 2026

---

## 0. General Principles

### Rules

- **Use English only** for all technical artifacts
- **Names must be clear, descriptive, and consistent**
- **Avoid unclear abbreviations or random naming**
- **Follow the agreed format exactly as specified**

---

## 1. Layer-Based Package Structure

Use layered architecture naming:

```
com.floodrescue.backend.<module>.<layer>
```

### Modules

| Module   | Description                              |
|----------|------------------------------------------|
| auth     | Authentication & Authorization           |
| citizen  | Citizen-related features (SOS requests)  |
| rescue   | Rescue operations & mission management   |
| manager  | Resource management (Inventory, Vehicles)|
| admin    | System administration                    |
| common   | Shared logic across modules              |

### Layers

| Layer      | Purpose                    |
|------------|----------------------------|
| controller | Handle API requests        |
| service    | Business logic             |
| repository | Data access                |
| model      | Entity / DTO               |
| config     | Configuration              |
| exception  | Error handling             |
| util       | Helper utilities           |

### Example:

```
com.floodrescue.backend.citizen.controller
com.floodrescue.backend.rescue.service
com.floodrescue.backend.manager.repository
com.floodrescue.backend.common.util
```

---

## 2. Class Naming Convention

### 2.1 Controller Classes

**Format:** `<Feature>Controller`

**Example:**

```java
RequestController
MissionController
WarehouseController
RescueTeamController
```

### 2.2 Service Classes

**Format:**
- `<Feature>Service`
- `<Feature>ServiceImpl` (if implementation is separated)

**Example:**

```java
RequestService
MissionAssignmentService
ReliefDistributionServiceImpl
NotificationServiceImpl
```

### 2.3 Repository Classes

**Format:** `<Entity>Repository`

**Example:**

```java
UserRepository
RequestRepository
MissionRepository
RescueTeamRepository
InventoryRepository
```

### 2.4 Model / Entity Classes

**Format:** `<Entity>`

**Example:**

```java
User
Request
Mission
RescueTeam
Vehicle
Warehouse
Inventory
Item
ReliefDistribution
Feedback
Notification
```

### 2.5 DTO (Data Transfer Object)

**Format:**
- `<Entity><Action>Request`
- `<Entity><Action>Response`

**Example:**

```java
CreateRequestRequest
RequestDetailResponse
AssignMissionRequest
MissionStatusUpdateRequest
DistributeReliefRequest
TeamPositionResponse
```

### 2.6 Exception Classes

**Format:** `<Error>Exception`

**Example:**

```java
ResourceNotFoundException
UnauthorizedAccessException
MissionNotAssignedException
InsufficientInventoryException
InvalidRequestStatusException
```

---

## 3. Method Naming Convention

### 3.1 CRUD Operations

| Action | Method Name          |
|--------|----------------------|
| Create | `create()`           |
| Read   | `get()` / `getAll()` |
| Update | `update()`           |
| Delete | `delete()`           |

**Example:**

```java
createRequest()
getMissionById()
updateMissionStatus()
deleteNotification()
getAllActiveTeams()
```

### 3.2 Status & Workflow Methods

**Format:**
- `assign<Actor>()`
- `update<Status>()`
- `validate<Condition>()`

**Example:**

```java
assignRescueTeam()
updateRequestStatus()
validateInventoryAvailability()
dispatchMission()
distributeRelief()
trackTeamPosition()
```

---

## 4. Variable Naming Convention

### Rules

- Use **camelCase**
- Avoid single-letter variables (except loop counters)
- Boolean variables start with **is/has/can**

**Example:**

```java
isCompleted
hasMissionAssigned
canDispatchTeam
requestPriority
teamLocation
distributedQuantity
```

---

## 5. Constant & Enum Naming

### 5.1 Constants

**Format:** `UPPER_SNAKE_CASE`

**Example:**

```java
MAX_RESCUE_TEAMS_PER_MISSION
DEFAULT_WAREHOUSE_CAPACITY
CRITICAL_RESPONSE_TIME_MINUTES
API_VERSION
```

### 5.2 Enums

**Format:** `PascalCase`

**Example:**

```java
public enum RequestType {
    RESCUE,
    RELIEF
}

public enum MissionStatus {
    PENDING,
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

public enum Priority {
    CRITICAL,
    HIGH,
    NORMAL
}

public enum ItemType {
    FOOD,
    DRINK,
    MEDICAL_SUPPLIES
}

public enum VehicleStatus {
    AVAILABLE,
    IN_USE,
    MAINTENANCE
}
```

---

## 6. Configuration Naming

### 6.1 Config Classes

**Format:** `<Feature>Config`

**Example:**

```java
SecurityConfig
DatabaseConfig
SwaggerConfig
WebSocketConfig
FirebaseConfig
```

### 6.2 Environment Variables

**Format:** `UPPER_SNAKE_CASE`

**Example:**

```
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION
FIREBASE_API_KEY
API_BASE_URL
WEBSOCKET_ENDPOINT
```

---

## 7. Database Naming Convention

### 7.1 Table Naming

**Format:** `snake_case` (plural)

**Example:**

```sql
users
roles
requests
missions
rescue_teams
team_members
vehicles
vehicle_depots
mission_vehicles
warehouses
inventories
items
relief_distributions
mission_assignments
reports
notifications
feedbacks
team_positions
```

### 7.2 Column Naming

**Format:** `snake_case`

**Example:**

```sql
id
full_name
phone_number
email
password_hash
role_id
is_active
created_at
mission_type
request_type
priority
status
latitude
longitude
quantity_distributed
distributed_at
```

### 7.3 Primary & Foreign Keys

**Format:**
- Primary Key: `id` or `<entity>_id`
- Foreign Key: `<entity>_id`

**Example:**

```sql
-- Primary Keys
id
vehicle_id
depot_id
position_id

-- Foreign Keys
user_id
mission_id
rescue_team_id
warehouse_id
item_id
request_id
```

---

## 8. API Endpoint Naming Convention

### REST API Rules

- Use **nouns, not verbs**
- Use **plural**
- Use **kebab-case**
- Include API version

### Example:

```
POST   /api/v1/rescue-requests
GET    /api/v1/rescue-requests/{id}
PUT    /api/v1/rescue-requests/{id}/status
DELETE /api/v1/rescue-requests/{id}

POST   /api/v1/missions
GET    /api/v1/missions/{id}
PUT    /api/v1/missions/{id}/assign-team
PATCH  /api/v1/missions/{id}/status

GET    /api/v1/rescue-teams
POST   /api/v1/rescue-teams/{id}/positions
GET    /api/v1/rescue-teams/{id}/members

POST   /api/v1/relief-distributions
GET    /api/v1/relief-distributions/mission/{missionId}

GET    /api/v1/warehouses
GET    /api/v1/warehouses/{id}/inventory
PUT    /api/v1/warehouses/{id}/inventory/update

GET    /api/v1/vehicles
PUT    /api/v1/vehicles/{id}/status

GET    /api/v1/notifications
PATCH  /api/v1/notifications/{id}/read
```

---

## 9. Front-End Naming

### 9.1 Component Naming

**Format:** `PascalCase`

**Example:**

```javascript
RequestForm
MissionDashboard
TeamLocationMap
ReliefDistributionCard
NotificationBadge
StatusIndicator
WarehouseInventoryTable
VehicleStatusBadge
```

### 9.2 UI State Variables

**Format:** `camelCase`

**Example:**

```javascript
isLoading
hasError
selectedMissionType
activeMissions
currentLocation
isTeamAvailable
inventoryItems
distributionProgress
```

---

## 10. Test Naming Convention

### 10.1 Test Classes

**Format:** `<ClassName>Test`

**Example:**

```java
RequestServiceTest
MissionControllerTest
RescueTeamRepositoryTest
ReliefDistributionServiceTest
```

### 10.2 Test Methods

**Format:** `should<ExpectedBehavior>When<Condition>()`

**Example:**

```java
shouldCreateRequestWhenDataIsValid()
shouldAssignTeamWhenMissionIsPending()
shouldRejectDistributionWhenInventoryInsufficient()
shouldUpdateStatusWhenMissionCompleted()
shouldNotifyUserWhenMissionDispatched()
```

---

## 11. Logging Naming Convention

### Logger Variable

**Format:** `logger`

### Log Messages

- Start with the **module name** in brackets
- Be **descriptive**

**Example:**

```java
[AUTH] User login attempt: {email}
[CITIZEN] New rescue request submitted: {requestId}
[RESCUE] Mission {missionId} assigned to team {teamId}
[MANAGER] Inventory updated: {itemName} quantity: {quantity}
[RESCUE] Team position updated: lat={lat}, lon={lon}
[MANAGER] Relief distribution completed: {distributionId}
```

---

## 12. Git Commit Naming Convention

### Format

```
[TASK-ID] <Type>: <Short Description>
```

### Task ID Categories

**Business Logic:**
- `AUTH-XX`: Authentication & Authorization
- `CZ-XX`: Citizen features
- `RC-XX`: Rescue operations
- `MG-XX`: Manager/Resource features
- `RT-XX`: Reporting features
- `AD-XX`: Admin features

**Technical Process:**
- `REQ-XX`: Requirements analysis
- `DB-XX`: Database design
- `UI-XX`: UI/UX design
- `ARCH-XX`: Architecture design
- `ENV-XX`: Environment setup
- `DOC-XX`: Documentation
- `TEST-XX`: Testing
- `DEPLOY-XX`: Deployment
- `PRESENT-XX`: Presentation

### Commit Types

| Type     | Description                              |
|----------|------------------------------------------|
| feat     | New feature                              |
| fix      | Bug fix                                  |
| refactor | Code restructuring (no behavior change)  |
| docs     | Documentation update                     |
| test     | Test-related changes                     |
| chore    | Configuration, build, tooling            |

### Example:

```
[CZ-01] feat: add api to submit sos request
[RC-03] feat: implement mission assignment logic
[MG-02] feat: add warehouse inventory management
[AUTH-01] feat: implement jwt authentication
[DB-05] refactor: optimize mission query performance
[UI-04] feat: create team location tracking map
[TEST-02] test: add unit tests for relief distribution
[DOC-01] docs: update api documentation for requests
[DEPLOY-03] chore: configure production environment
```

---

## 13. Git Branch Naming Convention

### Format

```
<type>/<task-id>-<short-description>
```

### Branch Types

| Type     | Usage              |
|----------|--------------------|
| feature  | New functionality  |
| bugfix   | Bug fixing         |
| docs     | Documentation      |
| refactor | Code improvement   |

### Example:

```
feature/cz-01-sos-request-api
feature/rc-03-mission-assignment
feature/mg-02-warehouse-inventory
feature/auth-01-jwt-authentication
bugfix/rc-05-mission-status-update
docs/doc-01-api-documentation
refactor/db-05-optimize-queries
```

---

## 14. API / JSON Naming Convention

### Rule

- Use **camelCase**
- Use **meaningful field names**

### Example:

```json
{
  "requestId": 123,
  "requestType": "RESCUE",
  "priority": "CRITICAL",
  "latitude": 10.7626,
  "longitude": 106.6821,
  "description": "Family trapped on rooftop",
  "status": "PENDING",
  "createdAt": "2026-01-25T10:30:00Z",
  "citizen": {
    "userId": 456,
    "fullName": "Nguyen Van A",
    "phoneNumber": "+84901234567"
  }
}
```

```json
{
  "missionId": 789,
  "missionType": "RESCUE",
  "status": "IN_PROGRESS",
  "assignedTeam": {
    "teamId": 12,
    "teamName": "Alpha Rescue Team",
    "memberCount": 5
  },
  "currentPosition": {
    "latitude": 10.7650,
    "longitude": 106.6830,
    "recordedAt": "2026-01-25T11:15:00Z"
  }
}
```

---

## 15. Diagram Naming Convention

### Format

```
<diagram-type>_<module>_<description>
```

### Example:

```
usecase_citizen_sos_request
usecase_rescue_mission_dispatch
sequence_rescue_team_assignment
sequence_relief_distribution
activity_mission_workflow
activity_inventory_management
erd_flood_rescue_system
class_rescue_team_module
deployment_system_architecture
```

---

## 16. File & Asset Naming Convention

### Format

```
lowercase-with-hyphen
```

### Example:

**Images:**
```
icon-sos.png
icon-mission.svg
logo-flood-rescue.png
bg-dashboard.jpg
map-marker-team.svg
```

**Documents:**
```
srs-flood-rescue-system.pdf
api-documentation-v1.pdf
deployment-guide.md
user-manual.pdf
```

**Scripts:**
```
setup-database.sh
deploy-backend.sh
seed-initial-data.sql
```

---

## Summary Checklist

- [ ] All packages follow `com.floodrescue.backend.<module>.<layer>`
- [ ] All classes use PascalCase
- [ ] All methods use camelCase
- [ ] All database tables use snake_case (plural)
- [ ] All API endpoints use kebab-case (plural nouns)
- [ ] All environment variables use UPPER_SNAKE_CASE
- [ ] All git commits follow `[TASK-ID] type: description`
- [ ] All git branches follow `type/task-id-description`
- [ ] All JSON fields use camelCase
- [ ] All constants use UPPER_SNAKE_CASE
- [ ] All boolean variables start with is/has/can
- [ ] All file names use lowercase-with-hyphen

---

**End of Document**
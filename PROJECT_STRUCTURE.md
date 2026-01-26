# Cấu Trúc Dự Án Flood Rescue System - Backend

## 📁 Sơ Đồ Cây Thư Mục Hoàn Chỉnh

```
flood-rescue-backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── floodrescue/
│   │   │           └── backend/
│   │   │               ├── BackendApplication.java
│   │   │               │
│   │   │               ├── auth/                          # Module Authentication & Authorization
│   │   │               │   ├── controller/
│   │   │               │   │   └── AuthController.java
│   │   │               │   ├── service/
│   │   │               │   │   ├── AuthService.java
│   │   │               │   │   └── AuthServiceImpl.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── UserRepository.java
│   │   │               │   │   └── RoleRepository.java
│   │   │               │   ├── model/
│   │   │               │   │   ├── User.java
│   │   │               │   │   └── Role.java
│   │   │               │   └── dto/
│   │   │               │       ├── LoginRequest.java
│   │   │               │       ├── LoginResponse.java
│   │   │               │       ├── RegisterRequest.java
│   │   │               │       └── RegisterResponse.java
│   │   │               │
│   │   │               ├── citizen/                       # Module Citizen (SOS Requests)
│   │   │               │   ├── controller/
│   │   │               │   │   └── RequestController.java
│   │   │               │   ├── service/
│   │   │               │   │   ├── RequestService.java
│   │   │               │   │   └── RequestServiceImpl.java
│   │   │               │   ├── repository/
│   │   │               │   │   └── RequestRepository.java
│   │   │               │   ├── model/
│   │   │               │   │   └── Request.java
│   │   │               │   └── dto/
│   │   │               │       ├── CreateRequestRequest.java
│   │   │               │       └── RequestDetailResponse.java
│   │   │               │
│   │   │               ├── rescue/                        # Module Rescue Operations
│   │   │               │   ├── controller/
│   │   │               │   │   └── MissionController.java
│   │   │               │   ├── service/
│   │   │               │   │   ├── MissionService.java
│   │   │               │   │   └── MissionServiceImpl.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── MissionRepository.java
│   │   │               │   │   └── RescueTeamRepository.java
│   │   │               │   ├── model/
│   │   │               │   │   ├── Mission.java
│   │   │               │   │   ├── RescueTeam.java
│   │   │               │   │   ├── TeamMember.java
│   │   │               │   │   ├── MissionAssignment.java
│   │   │               │   │   ├── TeamPosition.java
│   │   │               │   │   └── Report.java
│   │   │               │   └── dto/
│   │   │               │       ├── AssignMissionRequest.java
│   │   │               │       ├── MissionDetailResponse.java
│   │   │               │       └── MissionStatusUpdateRequest.java
│   │   │               │
│   │   │               ├── manager/                       # Module Manager (Resources)
│   │   │               │   ├── controller/
│   │   │               │   │   ├── VehicleController.java
│   │   │               │   │   └── WarehouseController.java
│   │   │               │   ├── service/
│   │   │               │   │   ├── VehicleService.java
│   │   │               │   │   ├── VehicleServiceImpl.java
│   │   │               │   │   ├── WarehouseService.java
│   │   │               │   │   └── WarehouseServiceImpl.java
│   │   │               │   ├── repository/
│   │   │               │   │   ├── VehicleRepository.java
│   │   │               │   │   ├── WarehouseRepository.java
│   │   │               │   │   └── InventoryRepository.java
│   │   │               │   ├── model/
│   │   │               │   │   ├── Vehicle.java
│   │   │               │   │   ├── VehicleDepot.java
│   │   │               │   │   ├── Warehouse.java
│   │   │               │   │   ├── Item.java
│   │   │               │   │   ├── Inventory.java
│   │   │               │   │   ├── ReliefDistribution.java
│   │   │               │   │   └── MissionVehicle.java
│   │   │               │   └── dto/
│   │   │               │       ├── CreateVehicleRequest.java
│   │   │               │       ├── VehicleDetailResponse.java
│   │   │               │       ├── VehicleStatusUpdateRequest.java
│   │   │               │       ├── CreateWarehouseRequest.java
│   │   │               │       ├── WarehouseDetailResponse.java
│   │   │               │       └── WarehouseInventoryResponse.java
│   │   │               │
│   │   │               ├── admin/                         # Module Admin
│   │   │               │   ├── controller/
│   │   │               │   │   └── UserManagementController.java
│   │   │               │   ├── service/
│   │   │               │   │   ├── UserManagementService.java
│   │   │               │   │   └── UserManagementServiceImpl.java
│   │   │               │   ├── repository/
│   │   │               │   │   └── NotificationRepository.java
│   │   │               │   ├── model/
│   │   │               │   │   ├── Notification.java
│   │   │               │   │   └── Feedback.java
│   │   │               │   └── dto/
│   │   │               │       ├── UserDetailResponse.java
│   │   │               │       └── UserStatusUpdateRequest.java
│   │   │               │
│   │   │               └── common/                        # Module Common (Shared)
│   │   │                   ├── config/
│   │   │                   │   ├── CorsConfig.java
│   │   │                   │   └── SecurityConfig.java
│   │   │                   ├── exception/
│   │   │                   │   ├── GlobalExceptionHandler.java
│   │   │                   │   ├── ResourceNotFoundException.java
│   │   │                   │   ├── UnauthorizedAccessException.java
│   │   │                   │   ├── InvalidRequestStatusException.java
│   │   │                   │   └── InsufficientInventoryException.java
│   │   │                   ├── util/
│   │   │                   │   └── JwtUtils.java
│   │   │                   └── dto/
│   │   │                       └── ApiResponse.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── floodrescue/
│                   └── backend/
│                       └── BackendApplicationTests.java
│
├── docker-compose.yml
├── pom.xml
└── README.md
```

## 📊 Tổng Kết Các Module

### 1. **auth** - Authentication & Authorization
- **Entities**: User, Role
- **Controllers**: AuthController
- **Services**: AuthService, AuthServiceImpl
- **Repositories**: UserRepository, RoleRepository
- **DTOs**: LoginRequest, LoginResponse, RegisterRequest, RegisterResponse

### 2. **citizen** - Citizen SOS Requests
- **Entities**: Request
- **Controllers**: RequestController
- **Services**: RequestService, RequestServiceImpl
- **Repositories**: RequestRepository
- **DTOs**: CreateRequestRequest, RequestDetailResponse

### 3. **rescue** - Rescue Operations & Missions
- **Entities**: Mission, RescueTeam, TeamMember, MissionAssignment, TeamPosition, Report
- **Controllers**: MissionController
- **Services**: MissionService, MissionServiceImpl
- **Repositories**: MissionRepository, RescueTeamRepository
- **DTOs**: AssignMissionRequest, MissionDetailResponse, MissionStatusUpdateRequest

### 4. **manager** - Resource Management
- **Entities**: Vehicle, VehicleDepot, Warehouse, Item, Inventory, ReliefDistribution, MissionVehicle
- **Controllers**: VehicleController, WarehouseController
- **Services**: VehicleService, VehicleServiceImpl, WarehouseService, WarehouseServiceImpl
- **Repositories**: VehicleRepository, WarehouseRepository, InventoryRepository
- **DTOs**: CreateVehicleRequest, VehicleDetailResponse, VehicleStatusUpdateRequest, CreateWarehouseRequest, WarehouseDetailResponse, WarehouseInventoryResponse

### 5. **admin** - System Administration
- **Entities**: Notification, Feedback
- **Controllers**: UserManagementController
- **Services**: UserManagementService, UserManagementServiceImpl
- **Repositories**: NotificationRepository
- **DTOs**: UserDetailResponse, UserStatusUpdateRequest

### 6. **common** - Shared Components
- **Config**: CorsConfig, SecurityConfig
- **Exceptions**: GlobalExceptionHandler, ResourceNotFoundException, UnauthorizedAccessException, InvalidRequestStatusException, InsufficientInventoryException
- **Utils**: JwtUtils
- **DTOs**: ApiResponse

## 🗄️ Database Entities Mapping

Tất cả các entities đã được tạo theo ERD:

1. **User** → `users` table
2. **Role** → `roles` table
3. **Request** → `requests` table
4. **Mission** → `missions` table
5. **RescueTeam** → `rescue_teams` table
6. **TeamMember** → `team_members` table
7. **MissionAssignment** → `mission_assignments` table
8. **TeamPosition** → `team_positions` table
9. **Report** → `reports` table
10. **Vehicle** → `vehicles` table
11. **VehicleDepot** → `vehicle_depots` table
12. **MissionVehicle** → `mission_vehicles` table
13. **Warehouse** → `warehouses` table
14. **Item** → `items` table
15. **Inventory** → `inventories` table
16. **ReliefDistribution** → `relief_distributions` table
17. **Notification** → `notifications` table
18. **Feedback** → `feedbacks` table

## ⚙️ Configuration Files

- **application.properties**: Database connection, JPA configuration với CamelCaseToUnderscoresNamingStrategy
- **docker-compose.yml**: SQL Server 2019 container configuration
- **pom.xml**: Maven dependencies (đã thêm JWT libraries)

## ✅ Đã Hoàn Thành

- ✅ Tất cả package structure theo Modular MVC
- ✅ Tất cả Entity classes từ ERD
- ✅ Controllers, Services, Repositories cho mỗi module
- ✅ DTOs cho request/response
- ✅ Common utilities (JWT, Exception handling, Security)
- ✅ Database configuration với snake_case naming
- ✅ Docker Compose cho SQL Server 2019

## 📝 Lưu Ý

Tất cả các file đã được tạo với code khung (boilerplate). Logic nghiệp vụ cần được implement trong các method có comment `// TODO:`.

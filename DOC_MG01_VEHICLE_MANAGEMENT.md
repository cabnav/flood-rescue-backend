## MG-01 - Quản lý Phương tiện (Vehicle Management)

**Mục tiêu:** Cung cấp API cho Manager/Admin quản lý đội phương tiện cứu hộ: tạo mới, xem danh sách, cập nhật, xoá và theo dõi trạng thái sẵn sàng (`AVAILABLE`, `IN_USE`, `MAINTENANCE`).

---

## 1. Business Rules (Đã triển khai)

- **Rule 1 – Mỗi phương tiện luôn có đúng 1 trạng thái**
  - Trên model `Vehicle`:
    - Thuộc tính `status` là enum `Vehicle.VehicleStatus`, được lưu dưới dạng `@Enumerated(EnumType.STRING)` và `nullable = false`.
  - Khi **tạo mới** (`POST /api/v1/vehicles`), field `status` là **bắt buộc** trong `VehicleRequest`.

- **Rule 2 – Trạng thái hợp lệ**
  - Enum `VehicleStatus` hiện tại **chỉ có 3 giá trị**:
    - `AVAILABLE`
    - `IN_USE`
    - `MAINTENANCE`
  - Mọi parse từ String → Enum (khi cập nhật status, filter theo status) đều check bằng `valueOf(...)` và trả về `400 Bad Request` nếu FE truyền giá trị không hợp lệ.

- **Rule 3 – Bảo vệ phương tiện đang `IN_USE`**
  - Trong `VehicleServiceImpl`:
    - **Update core info** (`updateVehicle`):
      - Nếu `vehicle.getStatus() == IN_USE` → ném `BadRequestException("Vehicle is currently IN_USE and cannot be modified")`.
    - **Delete** (`deleteVehicle`):
      - Nếu `vehicle.getStatus() == IN_USE` → ném `BadRequestException("Vehicle is currently IN_USE and cannot be deleted")`.
  - Như vậy:
    - FE **không thể thay đổi thông tin cơ bản** (type, model, licensePlate, capacity, status) hoặc xoá xe khi nó đang phục vụ nhiệm vụ.

- **Rule 4 – Tracking / Lọc theo trạng thái**
  - Repository `VehicleRepository` có:
    - `List<Vehicle> findByStatus(Vehicle.VehicleStatus status)`
  - Controller expose endpoint:
    - `GET /api/v1/vehicles/status/{status}` → trả về tất cả phương tiện theo trạng thái (AVAILABLE / IN_USE / MAINTENANCE).

Tất cả các rule trên đã được implement trong code hiện tại.

---

## 2. Quy trình sử dụng cho Manager (Workflow)

### 2.1. Vai trò & Bảo mật

- Tất cả endpoint trong `VehicleController` đều được bảo vệ bằng:

  ```java
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  ```

- FE cần login bằng tài khoản có role `MANAGER` hoặc `ADMIN`, sau đó gọi API với header:

```http
Authorization: Bearer <JWT_TOKEN>
```

### 2.2. Quy trình gợi ý cho Manager

1. **Tạo phương tiện mới**
   - Gọi `POST /api/v1/vehicles` với `VehicleRequest`.
   - Set `status` ban đầu thường là `AVAILABLE` (sẵn sàng cho nhiệm vụ).

2. **Xem danh sách phương tiện & lọc theo trạng thái**
   - `GET /api/v1/vehicles` → xem toàn bộ fleet.
   - `GET /api/v1/vehicles/status/AVAILABLE` → xem xe sẵn sàng.
   - `GET /api/v1/vehicles/status/IN_USE` → xem xe đang tham gia nhiệm vụ.
   - `GET /api/v1/vehicles/status/MAINTENANCE` → xem xe đang bảo trì.

3. **Cập nhật thông tin phương tiện**
   - Gọi `PUT /api/v1/vehicles/{id}` với `VehicleRequest`:
     - Chỉnh sửa loại xe, biển số, capacity, model, **và có thể điều chỉnh status** khi xe đang **không IN_USE**.
   - Nếu xe đang `IN_USE`, backend trả về `400` với message thông báo không thể sửa.

4. **Cập nhật trạng thái sau nhiệm vụ**
   - Khi integrate với module Mission:
     - Khi assign xe vào nhiệm vụ → service nhiệm vụ nên gọi `updateStatus(id, IN_USE)`.
     - Khi nhiệm vụ hoàn thành:
       - Nếu xe vẫn hoạt động tốt → `updateStatus(id, AVAILABLE)`.
       - Nếu xe cần bảo trì → `updateStatus(id, MAINTENANCE)`.
   - Trên API:
     - Manager có thể dùng `PUT /api/v1/vehicles/{id}/status` để điều chỉnh trạng thái.

5. **Xoá phương tiện (khi không còn sử dụng)**
   - Gọi `DELETE /api/v1/vehicles/{id}`.
   - Nếu xe đang ở trạng thái `IN_USE` → backend chặn và trả về `400 Bad Request`.

---

## 3. API List (Danh sách Endpoint)

> Base URL (local dev): `http://localhost:8080/api/v1/vehicles`

| Endpoint                         | Method | Mô tả                                                                                   |
|----------------------------------|--------|----------------------------------------------------------------------------------------|
| `/api/v1/vehicles`              | POST   | Tạo mới một phương tiện (Manager/Admin).                                              |
| `/api/v1/vehicles/{id}`         | GET    | Lấy thông tin chi tiết một phương tiện.                                               |
| `/api/v1/vehicles`              | GET    | Lấy danh sách tất cả phương tiện.                                                     |
| `/api/v1/vehicles/{id}`         | PUT    | Cập nhật thông tin core của phương tiện (chặn nếu đang `IN_USE`).                     |
| `/api/v1/vehicles/{id}`         | DELETE | Xoá phương tiện (chặn nếu đang `IN_USE`).                                             |
| `/api/v1/vehicles/{id}/status`  | PUT    | Cập nhật **chỉ** trạng thái phương tiện (`AVAILABLE` / `IN_USE` / `MAINTENANCE`).     |
| `/api/v1/vehicles/status/{status}` | GET | Lấy danh sách phương tiện theo trạng thái (tracking fleet theo tình trạng hiện tại). |

Tất cả response đều bọc trong `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Vehicle created successfully",
  "data": { ...VehicleResponse }
}
```

---

## 4. DTO Chi Tiết

### 4.1. VehicleRequest (Body cho Create/Update)

```java
public class VehicleRequest {
    private Integer depotId;             // optional, reserved cho future Depot feature
    @NotBlank private String type;       // ví dụ: "Boat", "Truck", "Ambulance"
    private String model;                // ví dụ: "Rescue Boat 3000"
    @NotBlank private String licensePlate;
    @NotNull @Min(1) private Integer capacityPerson;
    @NotNull private Vehicle.VehicleStatus status; // AVAILABLE / IN_USE / MAINTENANCE
}
```

### 4.2. VehicleResponse (Data trả về FE)

```java
public class VehicleResponse {
    private Integer vehicleId;
    private Integer depotId;
    private String type;
    private String model;
    private String licensePlate;
    private Integer capacityPerson;
    private Vehicle.VehicleStatus status;
}
```

### 4.3. VehicleStatusUpdateRequest

```java
public class VehicleStatusUpdateRequest {
    private String status; // "AVAILABLE" | "IN_USE" | "MAINTENANCE"
}
```

FE sẽ truyền `status` dạng String, backend parse sang Enum và trả về `400` nếu sai format.

---

## 5. Business Logic – "IN_USE" Protection (Quan trọng cho FE)

Khi phương tiện đang ở trạng thái **`IN_USE`**:

- **Không được phép:**
  - **Update core info** qua `PUT /api/v1/vehicles/{id}`:
    - type, model, licensePlate, capacityPerson, status → request sẽ bị từ chối.
    - Backend trả về `400 Bad Request` với message:  
      `"Vehicle is currently IN_USE and cannot be modified"`.
  - **Delete** qua `DELETE /api/v1/vehicles/{id}`:
    - Backend trả về `400 Bad Request` với message:  
      `"Vehicle is currently IN_USE and cannot be deleted"`.

- **Được phép:**
  - Thay đổi trạng thái qua `PUT /api/v1/vehicles/{id}/status` (thường là từ:
    - `IN_USE` → `AVAILABLE` (khi nhiệm vụ xong).
    - `IN_USE` → `MAINTENANCE` (khi xe cần sửa chữa).

**Lưu ý cho FE:**

- Nếu nhận `400 Bad Request` với các message trên khi gọi update/delete:
  - Hãy hiển thị thông báo dạng:
    - `"Phương tiện đang được sử dụng trong nhiệm vụ, không thể chỉnh sửa/xoá."`
  - Gợi ý user: kết thúc nhiệm vụ hoặc đổi trạng thái trước (tuỳ theo UX thiết kế cho module Mission).

---

## 6. Trạng Thái Hoàn Thành

- **MG-01 - Vehicle Management Backend:** ✅ **100% Hoàn thành**
  - Model & Enum: Done.
  - Service (CRUD + business rules): Done.
  - Controller (secured, có filter theo status): Done.
  - Validation & Error handling (`ApiResponse`, `BadRequestException`, `ResourceNotFoundException`): Done.

Phía Frontend có thể bắt đầu tích hợp ngay các API trong tài liệu này kết hợp với `API_INTEGRATION_GUIDE.md` và Swagger UI (`/swagger-ui/index.html`).


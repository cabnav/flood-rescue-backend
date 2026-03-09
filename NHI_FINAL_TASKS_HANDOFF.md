# NHI — FINAL TASKS HANDOFF DOCUMENT
**Date:** 2026-03-05  
**Author:** Backend Lead (NHI)  
**Sprint:** Sprint 2 — Final Audit & Implementation

---

## 1. AUDIT SUMMARY

A comprehensive final audit was performed on all backend features. Results:

| # | Feature | Verdict |
|---|---------|---------|
| 1 | Auto-Lock Vehicle on Assignment | ✅ Already Implemented |
| 2 | Auto-Deduct Supplies on Assignment | ✅ Already Implemented |
| 3 | Vehicle Availability Check (per vehicle) | ✅ Already Implemented |
| 4 | CZ-04: Feedback Validation (all 4 checks) | ✅ Already Implemented |
| 5 | AD-03: Summary Statistics / Dashboard Report | ❌ Was Missing → **Fixed** |
| 6 | Fleet-wide "Any Vehicle Available?" Check | ❌ Was Missing → **Fixed** |

---

## 2. NEW FEATURES IMPLEMENTED

### 2.1 AD-03: Summary Statistics API

**API:** `GET /api/v1/reports/summary`  
**Roles:** `ADMIN`, `MANAGER`

Returns a `DashboardSummaryResponse` with:
- **Requests:** total, CREATED, IN_PROGRESS, COMPLETED, CANCELLED counts
- **Missions:** total, PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED counts
- **Rescue Impact:** total people rescued (SUM across all mission reports)
- **Vehicles:** total, AVAILABLE, IN_USE, MAINTENANCE counts

**Files created:**
| File | Type | Path |
|------|------|------|
| `DashboardSummaryResponse.java` | DTO | `admin/dto/` |
| `DashboardService.java` | Interface | `admin/service/` |
| `DashboardServiceImpl.java` | Implementation | `admin/service/` |
| `DashboardController.java` | Controller | `admin/controller/` |

**Files modified:**
| File | Change |
|------|--------|
| `RequestRepository.java` | Added `countByStatus(RequestStatus)` |
| `MissionRepository.java` | Added `countByStatus(MissionStatus)` |
| `ReportRepository.java` | Added `@Query SUM(peopleRescued)` via `sumPeopleRescued()` |
| `VehicleRepository.java` | Added `countByStatus(VehicleStatus)` |

---

### 2.2 Fleet-wide Vehicle Availability Alert

**API:** `GET /api/v1/vehicles/any-available`  
**Roles:** `RESCUE_COORDINATOR`, `MANAGER`

Returns `ApiResponse<Boolean>`:
- `true` — At least one vehicle with status `AVAILABLE` exists
- `false` — No vehicles are available (all IN_USE or MAINTENANCE)

**Files modified:**
| File | Change |
|------|--------|
| `VehicleService.java` | Added `isAnyVehicleAvailable()` method |
| `VehicleServiceImpl.java` | Implemented using `vehicleRepository.countByStatus(AVAILABLE) > 0` |
| `VehicleController.java` | Added `@GetMapping("/any-available")` endpoint |

---

## 3. CLEANUP PERFORMED

| File | Action |
|------|--------|
| `VehicleServiceImpl.java` | Removed unused `import java.util.stream.Collectors` |
| `.gitignore` | Updated to ignore all `*.md` (except `README.md`) and `*.txt` files |

---

## 4. PREVIOUSLY IMPLEMENTED FEATURES (Confirmed in Audit)

### 4.1 Auto-Lock Vehicle on Assignment
- **API:** `POST /api/v1/missions/{id}/assign-vehicle`
- **Behavior:** Checks `AVAILABLE` → sets vehicle to `IN_USE` → creates `MissionVehicle` record
- **Transactional:** Yes (`@Transactional`)
- **Auto-release:** Vehicles released to `AVAILABLE` on mission COMPLETED

### 4.2 Auto-Deduct Supplies on Assignment
- **API:** `POST /api/v1/missions/{id}/assign-supplies`
- **Behavior:** Checks sufficient stock → deducts quantity → creates `MissionSupply` record
- **Also checks:** Item must be `ACTIVE` (not `INACTIVE`)
- **Transactional:** Yes (`@Transactional`)

### 4.3 Vehicle Availability Check (Per Vehicle)
- **API:** `GET /api/v1/vehicles/check-availability?vehicleId=X`
- **Returns:** `ApiResponse<Boolean>` (true if vehicle status == AVAILABLE)

### 4.4 CZ-04: Feedback Validation
- **API:** `POST /api/v1/rescue-requests/{id}/feedback`
- **Validations:**
  - ✅ Blocks if Request is NOT `COMPLETED`
  - ✅ Blocks if user is NOT the original request owner
  - ✅ Blocks duplicate feedback (DB check + unique constraint fallback)
  - ✅ Also validates Mission is COMPLETED (if exists)

### 4.5 Admin/Manager Feedback Read APIs
- `GET /api/v1/feedbacks` — All feedbacks (sorted by newest)
- `GET /api/v1/feedbacks/user/{userId}` — Feedbacks by specific user

---

## 5. COMPLETE API LIST (All Endpoints)

| Method | Endpoint | Role(s) |
|--------|----------|---------|
| POST | `/api/v1/rescue-requests/rescue` | CITIZEN |
| POST | `/api/v1/rescue-requests/relief` | CITIZEN |
| GET | `/api/v1/rescue-requests/{id}` | ADMIN, COORDINATOR, TEAM, CITIZEN(owner) |
| GET | `/api/v1/rescue-requests` | COORDINATOR, ADMIN |
| GET | `/api/v1/rescue-requests/user/{userId}` | COORDINATOR, TEAM |
| PUT | `/api/v1/rescue-requests/{id}/status` | COORDINATOR, TEAM |
| PUT | `/api/v1/rescue-requests/{id}/approve` | COORDINATOR, TEAM |
| PUT | `/api/v1/rescue-requests/{id}/cancel` | COORDINATOR, TEAM |
| PATCH | `/api/v1/rescue-requests/{id}/classify` | COORDINATOR, ADMIN |
| POST | `/api/v1/rescue-requests/{id}/feedback` | CITIZEN (owner) |
| POST | `/api/v1/missions/request/{requestId}` | COORDINATOR |
| GET | `/api/v1/missions/{id}` | ADMIN, COORDINATOR, TEAM(assigned) |
| GET | `/api/v1/missions` | COORDINATOR, ADMIN |
| GET | `/api/v1/missions/assigned-to-me` | TEAM |
| PUT | `/api/v1/missions/{id}/assign-team` | COORDINATOR |
| PATCH | `/api/v1/missions/{id}/status` | COORDINATOR, ADMIN, TEAM |
| PATCH | `/api/v1/missions/assignments/{id}/response` | TEAM |
| POST | `/api/v1/missions/{id}/assign-vehicle` | COORDINATOR, MANAGER |
| POST | `/api/v1/missions/{id}/assign-supplies` | COORDINATOR, MANAGER |
| POST | `/api/v1/missions/{id}/report` | TEAM |
| POST | `/api/v1/vehicles` | MANAGER, ADMIN |
| GET | `/api/v1/vehicles/{id}` | MANAGER, ADMIN |
| GET | `/api/v1/vehicles` | MANAGER, ADMIN |
| PUT | `/api/v1/vehicles/{id}` | MANAGER, ADMIN |
| DELETE | `/api/v1/vehicles/{id}` | MANAGER, ADMIN |
| GET | `/api/v1/vehicles/status/{status}` | MANAGER, ADMIN |
| GET | `/api/v1/vehicles/check-availability` | COORDINATOR, MANAGER |
| **GET** | **`/api/v1/vehicles/any-available`** | **COORDINATOR, MANAGER** |
| GET | `/api/v1/feedbacks` | MANAGER, ADMIN |
| GET | `/api/v1/feedbacks/user/{userId}` | MANAGER, ADMIN |
| **GET** | **`/api/v1/reports/summary`** | **ADMIN, MANAGER** |

> **Bold** = Newly added in this handoff

---

## 6. NOTES

- All new endpoints follow existing project patterns (ApiResponse wrapper, @PreAuthorize, Spring Data JPA derived queries).
- The `sumPeopleRescued()` query uses `COALESCE` to safely return `0` when no reports exist.
- The `.gitignore` now ignores all `.md` files except `README.md`, and all `.txt` files.

---

## 7. STRICT VEHICLE STATUS WORKFLOW (LATEST UPDATE)

> **Message to Frontend Team:** 
> The `PUT /api/v1/vehicles/{id}/status` API has been completely **removed** to eliminate data inconsistency. 
> Vehicle statuses are now **100% automated** tied to the Mission Lifecycle.

**Workflow Behavior:**
- **Creation/Update**: Frontend can no longer send `status` in payload. All new vehicles default to `AVAILABLE`.
- **Status `MAINTENANCE`**: Completely deleted from system.
- **Dispatch (IN_USE)**: Vehicle automatically locks to `IN_USE` when assigned to a Mission.
- **Release (AVAILABLE)**: Vehicle automatically releases to `AVAILABLE` when the Mission is either `COMPLETED` or `CANCELLED`.
- **API Payload**: `VehicleResponse` now automatically returns `currentMissionId` and `currentRequestId` when a vehicle is `IN_USE` to help FE map UI context.

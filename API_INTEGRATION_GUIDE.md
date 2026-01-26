# API Integration Guide - Flood Rescue Backend

**Document Version:** 1.0  
**Target Audience:** Lead Frontend Developer (Trần Xuân)  
**Backend Framework:** Spring Boot 3 with JWT Authentication  
**Server Port:** 8080

---

## I. Environment Variables (Required FE Setup)

### Base URL Configuration

Create a `.env` file in your React/Vite project root with the following:

```env
VITE_REACT_APP_API_BASE_URL=http://localhost:8080/api/v1
```

**For Production:**
```env
VITE_REACT_APP_API_BASE_URL=https://your-production-domain.com/api/v1
```

**Usage in Frontend Code:**
```javascript
const API_BASE_URL = import.meta.env.VITE_REACT_APP_API_BASE_URL;
```

---

## II. API Contract & Endpoints

All endpoints follow the base path: `/api/v1`

### Response Format

All API responses follow this structure:

```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
```

---

### 1. Register User

**Endpoint:** `POST /api/v1/auth/register`

**URL:** `{BASE_URL}/auth/register`

**Request Body:**
```typescript
interface RegisterRequest {
  fullName: string;
  email: string;
  phoneNumber?: string;
  password: string;
  role?: string;  // Optional, defaults to "USER"
}
```

**Response (200 OK):**
```typescript
ApiResponse<{
  userId: number;
  email: string;
  fullName: string;
  role: string;
}>
```

**Example Request:**
```javascript
const response = await fetch(`${API_BASE_URL}/auth/register`, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    fullName: 'John Doe',
    email: 'john@example.com',
    phoneNumber: '+84123456789',
    password: 'SecurePassword123!',
    role: 'USER'
  })
});
```

---

### 2. Login

**Endpoint:** `POST /api/v1/auth/login`

**URL:** `{BASE_URL}/auth/login`

**Request Body:**
```typescript
interface LoginRequest {
  email: string;
  password: string;
}
```

**Response (200 OK):**
```typescript
ApiResponse<{
  token: string;           // JWT access token
  refreshToken: string | null;  // Currently null, reserved for future
  userId: number;
  email: string;
  role: string;            // User role (e.g., "ADMIN", "USER", "MANAGER")
}>
```

**Example Request:**
```javascript
const response = await fetch(`${API_BASE_URL}/auth/login`, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    email: 'admin@floodrescue.com',
    password: 'AdminPass2026'
  })
});

const result = await response.json();
// Store token: localStorage.setItem('token', result.data.token);
```

**Error Responses:**
- `401 Unauthorized`: Invalid email or password
- `400 Bad Request`: Missing required fields

---

### 3. Get Current User Profile

**Endpoint:** `GET /api/v1/users/me`

**URL:** `{BASE_URL}/users/me`

**Headers Required:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```typescript
ApiResponse<{
  id: number;
  email: string;
  fullName: string;
  phoneNumber?: string;
  role: string;
  isActive: boolean;
  createdAt: string;  // ISO 8601 format
  avatar64?: string;
}>
```

**Example Request:**
```javascript
const token = localStorage.getItem('token');
const response = await fetch(`${API_BASE_URL}/users/me`, {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json',
  }
});

const result = await response.json();
```

**Error Responses:**
- `401 Unauthorized`: Missing or invalid token
- `403 Forbidden`: Token valid but insufficient permissions

---

## III. Essential Technical Notes (Integration Instructions)

### Note 1: Authorization Header Format

**CRITICAL:** All authenticated endpoints require the JWT token in the `Authorization` header with the exact format:

```
Authorization: Bearer <token>
```

**Important Points:**
- The word "Bearer" must be capitalized
- There must be exactly **one space** between "Bearer" and the token
- Do NOT include quotes around the token
- The token is obtained from the login response (`data.token`)

**Correct Example:**
```javascript
headers: {
  'Authorization': `Bearer ${token}`  // ✅ Correct
}
```

**Incorrect Examples:**
```javascript
'Authorization': `bearer ${token}`      // ❌ Wrong: lowercase "bearer"
'Authorization': `Bearer${token}`      // ❌ Wrong: no space
'Authorization': `Bearer "${token}"`   // ❌ Wrong: quotes around token
'Authorization': token                 // ❌ Wrong: missing "Bearer " prefix
```

---

### Note 2: 403 Forbidden Status Handling

**Business Rule:** The backend enforces role-based access control (RBAC). When a user attempts to access an endpoint that requires a specific role, the server will return:

**HTTP Status:** `403 Forbidden`

**Response Body:**
```typescript
{
  success: false,
  message: "Access denied. Insufficient permissions.",
  data: null
}
```

**Implementation Requirement:**

You **MUST** implement a check for `403` status in your API client/interceptor:

```javascript
// Example with Axios interceptor
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 403) {
      // Handle 403: Redirect to unauthorized page or show error message
      // Optionally: Clear token and redirect to login
      localStorage.removeItem('token');
      window.location.href = '/unauthorized';
    }
    return Promise.reject(error);
  }
);
```

**Why This Matters:**
- A `401` means the token is missing/invalid → redirect to login
- A `403` means the token is valid but the user lacks permission → show "Access Denied" page
- **Never treat 403 as a login issue** - the user is authenticated but not authorized

---

### Note 3: Official Test Credentials

**Admin User (For Testing):**

```
Email: admin@floodrescue.com
Password: AdminPass2026
Role: ADMIN
```

**Usage:**
1. Use these credentials to test the login endpoint
2. Store the returned token for testing authenticated endpoints
3. Admin role has access to all endpoints including `/api/v1/admin/**`

**Security Note:**
- These are development/test credentials only
- **DO NOT** hardcode these credentials in production code
- Use environment variables for different environments (dev/staging/prod)

---

## IV. Additional Integration Tips

### Token Storage

**Recommended Approach:**
```javascript
// After successful login
const loginResponse = await login(email, password);
localStorage.setItem('token', loginResponse.data.token);
localStorage.setItem('userId', loginResponse.data.userId.toString());
localStorage.setItem('userRole', loginResponse.data.role);
```

### Token Expiration

- JWT tokens expire after **24 hours** (86400000 milliseconds)
- Implement token refresh logic or redirect to login when token expires
- Check for `401 Unauthorized` responses to detect expired tokens

### CORS Configuration

The backend is configured to accept requests from:
- `http://localhost:*` (all ports)
- `http://127.0.0.1:*`
- `http://192.168.*.*:*` (local network)
- `https://*.ngrok-free.dev` (for tunneling)

If you encounter CORS issues, ensure your frontend origin matches one of these patterns.

### Error Handling Best Practices

```javascript
async function apiCall(url, options = {}) {
  try {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(localStorage.getItem('token') && {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }),
        ...options.headers,
      },
    });

    if (!response.ok) {
      if (response.status === 401) {
        // Token invalid/expired → redirect to login
        handleUnauthorized();
      } else if (response.status === 403) {
        // Insufficient permissions → show access denied
        handleForbidden();
      } else {
        // Other errors
        const error = await response.json();
        throw new Error(error.message || 'Request failed');
      }
    }

    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
}
```

---

## V. Quick Reference

| Endpoint | Method | Auth Required | Role Required |
|----------|--------|---------------|---------------|
| `/api/v1/auth/register` | POST | No | - |
| `/api/v1/auth/login` | POST | No | - |
| `/api/v1/users/me` | GET | Yes | Any authenticated user |

---

## Support & Contact

For API-related questions or issues, contact the Backend Team.

**Last Updated:** 2026-01-26

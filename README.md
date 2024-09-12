# User Authentication API with JWT

## Task

Build a User Authentication API using JWT (Access & Refresh Tokens) to handle user sign-up, login, logout, token refresh, and user information retrieval.

## Objective

Create a REST API that:
- Allows users to sign up, log in, and log out.
- Uses JWT for authentication and authorization.
- Implements Access and Refresh tokens for session management.
- Provides a secure API for retrieving user information once authenticated.

## Requirements

### 1. Sign-up API
- **Endpoint:** `POST /api/v1/auth/signup`
- **Description:** Create a new user with a username, email, and password.
- **Password Security:** Hash the password before saving it to the database.

### 2. Login API
- **Endpoint:** `POST /api/v1/auth/login`
- **Description:** Authenticate the user using their email and password.
- **Response:** Generate and return both Access and Refresh tokens after successful login.

### 3. Logout API
- **Endpoint:** `POST /api/v1/auth/logout`
- **Description:** Invalidate the refresh token.
- **Implementation:** Store invalid tokens in a blacklist or delete them from the database.

### 4. Refresh Token API
- **Endpoint:** `POST /api/v1/auth/refresh-token`
- **Description:** Provide a route to refresh the Access token using the Refresh token.
- **Token Expiry:**
  - **Access Token:** Short expiry time (e.g., 15 minutes).
  - **Refresh Token:** Longer expiry time (e.g., 7 days).

### 5. User Info API (Protected API)
- **Endpoint:** `GET /api/v1/user_info`
- **Description:** Return the authenticated user's information (e.g., username, email) when accessed with a valid Access token.
- **Error Handling:** Return a 401 Unauthorized error if the Access token is expired or invalid.

## Constraints

- **JWT Usage:** Use JWT for generating both Access and Refresh tokens.
  - **Access Token:** Include user information and set a short expiration time.
  - **Refresh Token:** Use to generate new Access tokens without requiring login and set a longer expiration time.

## Bonus

- **Role-Based Access Control:** Implement role-based access control to differentiate between regular users and admins.

## Technology Requirements

- **Backend Framework:** Any backend framework (e.g., Spring Boot, Node.js, Express, etc.).
- **Database:** Use PostgreSQL to store user credentials and tokens if needed.

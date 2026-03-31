# Kaffe - Technical Design & Architecture Document

## Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [System Architecture](#system-architecture)
4. [Data Models](#data-models)
5. [Authentication & Authorization](#authentication--authorization)
6. [API Endpoints](#api-endpoints)
7. [Security Implementation](#security-implementation)
8. [Database Design](#database-design)
9. [Deployment & Configuration](#deployment--configuration)

---

## 1. Project Overview

**Kaffe** is a Spring Boot-based e-commerce platform for coffee products. It provides user authentication, product catalog management, shopping cart functionality, order management, and account management with role-based access control.

### Key Features:
- User authentication and authorization (JWT-based)
- Role-based access control (CUSTOMER, ADMIN, EMPLOYEE)
- Product catalog with categories
- Shopping cart and order management
- User account and address management
- Responsive web interface with Thymeleaf templates

---

## 2. Technology Stack

### Backend:
- **Framework**: Spring Boot 4.0.3
- **Language**: Java 17
- **ORM**: Spring Data JPA with Hibernate 7.2.4
- **Security**: Spring Security 7.0.5 with JWT (JJWT 0.12.6)
- **Build Tool**: Gradle 7.x

### Database:
- **Primary**: PostgreSQL 14
- **Provider**: Supabase (PostgreSQL as a service)
- **Connection Pooling**: HikariCP 7.0.2

### Frontend:
- **Template Engine**: Thymeleaf 3.x
- **Extras**: Thymeleaf Spring Security 6
- **Markup**: HTML5

### Testing:
- **Framework**: JUnit 5
- **Spring Test Support**: Spring Boot Starter Test
- **H2 Database**: For in-memory testing

### Utilities:
- **Lombok**: For reducing boilerplate code
- **JWT**: io.jsonwebtoken for token generation and validation

---

## 3. System Architecture

### Layered Architecture Pattern

```
┌─────────────────────────────────────────────────────────┐
│              Presentation Layer (Web)                    │
│   Controllers, View Templates, Error Handling            │
├─────────────────────────────────────────────────────────┤
│            Application/Service Layer                     │
│   Business Logic, Data Transformation, Validation        │
├─────────────────────────────────────────────────────────┤
│          Data Access Layer (Persistence)                 │
│   Repositories, JPA Queries, Database Operations         │
├─────────────────────────────────────────────────────────┤
│              Database Layer (PostgreSQL)                 │
│   Supabase PostgreSQL Database                           │
└─────────────────────────────────────────────────────────┘
```

### Request Flow Architecture

```mermaid
sequenceDiagram
    participant Client as Client/Browser
    participant Filter as JWT Filter
    participant Controller as Controller
    participant Service as Service
    participant Repository as Repository
    participant Database as Database

    Client->>Filter: HTTP Request with JWT Cookie
    activate Filter
    Filter->>Filter: Extract & Validate JWT Token
    alt Token Valid
        Filter->>Controller: Pass to Controller
        deactivate Filter
    else Token Invalid
        Filter-->>Client: 401 Unauthorized
        deactivate Filter
    end
    
    activate Controller
    Controller->>Service: Call Business Logic Method
    deactivate Controller
    
    activate Service
    Service->>Repository: Query/Persist Data via JPA
    deactivate Service
    
    activate Repository
    Repository->>Database: SQL Operations
    Database-->>Repository: Data Response
    deactivate Repository
    
    Service-->>Controller: Service Response (DTO)
    Controller-->>Client: HTTP Response (JSON/HTML)
```

### Component Diagram

```mermaid
graph TB
    subgraph "Presentation Layer"
        AC[AuthController]
        ACC[AccountController]
        PRC[ProductController]
        CRC[CartController]
        OC[OrderController]
        WC[WebController]
    end
    
    subgraph "Service Layer"
        AS[AuthService]
        ACS[AccountService]
        PRS[ProductService]
        CRS[CartService]
        OS[OrderService]
    end
    
    subgraph "Repository Layer"
        AR[AccountRepository]
        PR[ProductRepository]
        CR[CartRepository]
        OR[OrderRepository]
    end
    
    subgraph "Security & Utility"
        JWT[JwtUtil]
        SEC[SecurityConfig]
        FILTER[JwtAuthenticationFilter]
    end
    
    subgraph "Database Layer"
        PSQL[(PostgreSQL<br/>Supabase)]
    end
    
    AC -->|uses| AS
    AS -->|uses| AR
    AS -->|uses| JWT
    
    ACC -->|uses| ACS
    ACS -->|uses| AR
    
    PRC -->|uses| PRS
    PRS -->|uses| PR
    
    CRC -->|uses| CRS
    CRS -->|uses| CR
    
    OC -->|uses| OS
    OS -->|uses| OR
    
    AR --> PSQL
    PR --> PSQL
    CR --> PSQL
    OR --> PSQL
    
    FILTER -->|validates| JWT
    SEC -->|configures| FILTER
```

---

## 4. Data Models

### Entity Relationship Diagram

```mermaid
erDiagram
    ACCOUNT ||--o{ CART : has
    ACCOUNT ||--o{ ADDRESS : has
    ACCOUNT ||--o{ ORDER : places
    CATEGORY ||--o{ PRODUCT : contains
    PRODUCT ||--o{ CART_PRODUCT : added_to
    CART ||--o{ CART_PRODUCT : contains
    ORDER ||--o{ CART_PRODUCT : includes
    ADDRESS ||--o{ ORDER : ships_to

    ACCOUNT {
        UUID unique_id PK
        string username UK
        string password
        string full_name
        string number
        enum role
    }

    CATEGORY {
        UUID unique_id PK
        string name
        string description
    }

    PRODUCT {
        UUID unique_id PK
        UUID category_id FK
        string name
        string sku
        string size
        string image
        string description
    }

    CART {
        UUID unique_id PK
        UUID account_id FK
    }

    CART_PRODUCT {
        UUID unique_id PK
        UUID cart_id FK
        UUID product_id FK
        UUID order_id FK
        int quantity
        decimal price
    }

    ORDER {
        UUID unique_id PK
        UUID account_id FK
        UUID address_id FK
        string status
        timestamp created_at
    }

    ADDRESS {
        UUID unique_id PK
        UUID account_id FK
        string street
        string city
        string province
        string postal_code
        string country
        string phone
    }
```

### Entity Classes

#### 1. Account Entity
```
- uniqueId (UUID) - Primary Key
- username (String) - Unique
- password (String) - Encoded
- fullName (String)
- number (String)
- role (Role: CUSTOMER, ADMIN, EMPLOYEE)
- carts (One-to-Many)
- addresses (One-to-Many)
- orders (One-to-Many)
```

#### 2. Product Entity
```
- uniqueId (UUID) - Primary Key
- category (Many-to-One)
- name (String)
- sku (String)
- size (String)
- image (String) - Image URL
- description (String)
- cartProducts (One-to-Many)
```

#### 3. Cart & CartProduct Entities
```
Cart:
- uniqueId (UUID) - Primary Key
- account (Many-to-One)
- cartProducts (One-to-Many)

CartProduct:
- uniqueId (UUID) - Primary Key
- cart (Many-to-One)
- product (Many-to-One)
- order (Many-to-One)
- quantity (Integer)
- price (BigDecimal)
```

#### 4. Order Entity
```
- uniqueId (UUID) - Primary Key
- account (Many-to-One)
- address (Many-to-One)
- status (String) - PENDING, PROCESSING, SHIPPED, DELIVERED
- cartProducts (One-to-Many)
```

#### 5. Address Entity
```
- uniqueId (UUID) - Primary Key
- account (Many-to-One)
- street (String)
- city (String)
- province (String)
- postalCode (String)
- country (String)
- phone (String)
```

#### 6. Category Entity
```
- uniqueId (UUID) - Primary Key
- name (String)
- description (String)
- products (One-to-Many)
```

---

## 5. Authentication & Authorization

### Authentication Flow Diagram

```mermaid
sequenceDiagram
    participant User as User/Browser
    participant Controller as AuthController
    participant Service as AuthService
    participant Repo as AccountRepository
    participant Encoder as PasswordEncoder
    participant JWT as JwtUtil
    participant DB as Database

    User->>Controller: POST /login (username, password)
    Controller->>Service: login(LoginRequest)
    
    Service->>Service: authenticationManager.authenticate()
    Service->>Repo: findByUsername(username)
    Repo->>DB: Query Account
    DB-->>Repo: Account Entity
    Repo-->>Service: Optional<Account>
    
    Service->>Encoder: matches(password, encoded)
    Encoder-->>Service: boolean
    
    alt Authentication Success
        Service->>JWT: generateToken(username)
        JWT-->>Service: JWT Token String
        Service-->>Controller: AuthResponse(success, token)
        Controller-->>User: Set JWT Cookie + Redirect
    else Authentication Failure
        Service-->>Controller: AuthResponse(failure, error)
        Controller-->>User: Redirect to /login?error
    end
```

### Registration Flow Diagram

```mermaid
sequenceDiagram
    participant User as User/Browser
    participant Controller as AuthController
    participant Service as AuthService
    participant Repo as AccountRepository
    participant Encoder as PasswordEncoder
    participant JWT as JwtUtil
    participant DB as Database

    User->>Controller: POST /register (username, password, fullName, number)
    Controller->>Controller: request.validate()
    Controller->>Service: register(RegisterRequest)
    
    Service->>Repo: findByUsername(username)
    Repo->>DB: Query Account
    DB-->>Repo: Optional (empty or present)
    
    alt Username Already Exists
        Repo-->>Service: Optional.isPresent()
        Service-->>Controller: AuthResponse(failure, "USERNAME_EXISTS")
        Controller-->>User: Redirect to /register?error
    else Username Available
        Service->>Encoder: encode(password)
        Encoder-->>Service: Encoded Password
        Service->>Service: Create Account Entity
        Service->>Repo: save(account)
        Repo->>DB: INSERT Account
        DB-->>Repo: Saved Account
        Service->>JWT: generateToken(username)
        JWT-->>Service: JWT Token String
        Service-->>Controller: AuthResponse(success, token)
        Controller-->>User: Set JWT Cookie + Redirect to /
    end
```

### JWT Token Structure

```
Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "sub": "username",
  "iat": 1234567890,
  "exp": 1234654290
}

Signature: HMACSHA256(header + payload, secret)
```

### Authorization Levels

| Role     | Permissions                                    |
|----------|------------------------------------------------|
| CUSTOMER | Browse products, manage cart, place orders    |
| ADMIN    | Manage products, categories, view all orders  |
| EMPLOYEE | Process orders, manage shipments              |

### Security Configuration

```
- CSRF: Disabled (Stateless JWT authentication)
- Session: STATELESS (No server-side sessions)
- Endpoints:
  - Public: /, /menu, /about, /shipment, /login, /register, /css/**, /js/**, /images/**
  - Authenticated: /api/**
  - Permitted: Everything else (requires case-by-case handling)
```

---

## 6. API Endpoints

### Authentication Endpoints

| Method | Endpoint      | Description           | Auth Required | Roles    |
|--------|---------------|-----------------------|---------------|----------|
| GET    | /login        | Login page            | No            | Public   |
| POST   | /login        | Submit login          | No            | Public   |
| GET    | /register     | Registration page     | No            | Public   |
| POST   | /register     | Submit registration   | No            | Public   |
| GET    | /logout       | Logout                | Yes           | Authenticated |

### Account Endpoints

| Method | Endpoint      | Description           | Auth Required | Roles    |
|--------|---------------|-----------------------|---------------|----------|
| GET    | /api/account  | Get current account   | Yes           | Authenticated |
| PUT    | /api/account  | Update account        | Yes           | Authenticated |
| GET    | /api/addresses| Get user addresses    | Yes           | Authenticated |
| POST   | /api/addresses| Add new address       | Yes           | Authenticated |

### Product Endpoints

| Method | Endpoint           | Description               | Auth Required | Roles    |
|--------|--------------------|-----------------------|---------------|----------|
| GET    | /api/products      | List all products       | No            | Public   |
| GET    | /api/products/:id  | Get product details     | No            | Public   |
| POST   | /api/products      | Create product          | Yes           | ADMIN    |
| PUT    | /api/products/:id  | Update product          | Yes           | ADMIN    |
| DELETE | /api/products/:id  | Delete product          | Yes           | ADMIN    |

### Cart Endpoints

| Method | Endpoint           | Description               | Auth Required | Roles    |
|--------|--------------------|-----------------------|---------------|----------|
| GET    | /api/cart          | Get user cart           | Yes           | Authenticated |
| POST   | /api/cart/items    | Add item to cart        | Yes           | Authenticated |
| DELETE | /api/cart/items/:id| Remove item from cart   | Yes           | Authenticated |
| PUT    | /api/cart/items/:id| Update item quantity    | Yes           | Authenticated |

### Order Endpoints

| Method | Endpoint           | Description               | Auth Required | Roles    |
|--------|--------------------|-----------------------|---------------|----------|
| GET    | /api/orders        | Get user orders         | Yes           | Authenticated |
| POST   | /api/orders        | Create new order        | Yes           | Authenticated |
| GET    | /api/orders/:id    | Get order details       | Yes           | Authenticated |
| PUT    | /api/orders/:id    | Update order status     | Yes           | ADMIN, EMPLOYEE |

---

## 7. Security Implementation

### JWT Authentication Filter

The `JwtAuthenticationFilter` intercepts all HTTP requests and:
1. Extracts JWT token from cookies or Authorization header
2. Validates token signature and expiration
3. Loads user details from database
4. Creates authentication context
5. Passes request to next filter if valid

### Password Security

- **Algorithm**: BCrypt with strength 10
- **Salting**: Automatic (BCrypt includes salt)
- **Never stored in plain text**: Always encoded before persistence

### CORS & CSRF

- **CSRF**: Disabled (JWT tokens are not vulnerable to CSRF)
- **CORS**: Not explicitly configured (relies on same-origin policy)

### Cookie Security

JWT tokens stored in cookies with:
- `HttpOnly`: Prevents JavaScript access
- `Secure`: Only transmitted over HTTPS (production)
- `Path`: Restricted to application path
- `MaxAge`: 24 hours (86400 seconds)

---

## 8. Database Design

### Database: PostgreSQL 14 (Supabase)

### Tables

#### 1. account
```sql
CREATE TABLE account (
    unique_id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    number VARCHAR(20),
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 2. category
```sql
CREATE TABLE category (
    unique_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 3. product
```sql
CREATE TABLE product (
    unique_id UUID PRIMARY KEY,
    category_id UUID REFERENCES category(unique_id),
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) UNIQUE,
    size VARCHAR(50),
    image TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 4. cart
```sql
CREATE TABLE cart (
    unique_id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES account(unique_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 5. cart_product
```sql
CREATE TABLE cart_product (
    unique_id UUID PRIMARY KEY,
    cart_id UUID REFERENCES cart(unique_id) ON DELETE CASCADE,
    product_id UUID REFERENCES product(unique_id),
    order_id UUID,
    quantity INT DEFAULT 1,
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 6. address
```sql
CREATE TABLE address (
    unique_id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES account(unique_id) ON DELETE CASCADE,
    street VARCHAR(255),
    city VARCHAR(100),
    province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 7. order
```sql
CREATE TABLE "order" (
    unique_id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES account(unique_id),
    address_id UUID REFERENCES address(unique_id),
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Indexes

```sql
CREATE INDEX idx_account_username ON account(username);
CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_cart_account ON cart(account_id);
CREATE INDEX idx_order_account ON "order"(account_id);
CREATE INDEX idx_address_account ON address(account_id);
```

---

## 9. Deployment & Configuration

### Environment Configuration

**Development Profile**: `application-supabase.properties`

```properties
# Database Configuration
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://${SUPABASE_DB_HOST}:${SUPABASE_DB_PORT}/${SUPABASE_DB_NAME}?sslmode=require}
spring.datasource.username=${SUPABASE_DB_USER}
spring.datasource.password=${SUPABASE_DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_DDL_AUTO:update}
spring.jpa.open-in-view=false

# Connection Pool
spring.datasource.hikari.maximum-pool-size=${SUPABASE_DB_POOL_SIZE:10}

# JWT Security
jwt.secret=${JWT_SECRET}
jwt.expiration-ms=86400000
```

### Supabase Configuration

1. **Create Supabase Project**
   - Visit https://supabase.com
   - Create new project
   - Configure PostgreSQL database

2. **Get Connection Details**
   - Go to Project Settings > Database
   - Copy connection string
   - Extract host, port, username, password, database name

3. **Set Environment Variables**
   ```
   DATABASE_URL=postgresql://user:password@host:port/dbname?sslmode=require
   SUPABASE_DB_HOST=host
   SUPABASE_DB_PORT=5432
   SUPABASE_DB_NAME=postgres
   SUPABASE_DB_USER=user
   SUPABASE_DB_PASSWORD=password
   SUPABASE_DB_POOL_SIZE=10
   JWT_SECRET=<your-secret-key-at-least-256-bits>
   ```

4. **Alternative: .env.supabase File**
   ```
   Create file: .env.supabase
   DATABASE_URL=...
   SUPABASE_DB_HOST=...
   # etc.
   ```

### Build & Run

```bash
# Build
./gradlew clean build

# Run
./gradlew bootRun

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=supabase'

# Package JAR
./gradlew bootJar
```

### Production Deployment

1. **Build JAR**: `./gradlew bootJar`
2. **Set Environment Variables** on production server
3. **Run JAR**: `java -jar kaffe-0.0.1-SNAPSHOT.jar`
4. **Use Process Manager**: systemd, supervisor, or Docker

### Docker Deployment

```dockerfile
FROM eclipse-temurin:17-jdk
COPY build/libs/kaffe-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  kaffe:
    build: .
    ports:
      - "8080:8080"
    environment:
      DATABASE_URL: ${DATABASE_URL}
      JWT_SECRET: ${JWT_SECRET}
      SPRING_PROFILES_ACTIVE: supabase
```

---

## 10. Data Transfer Objects (DTOs)

### Request DTOs

#### LoginRequest
```java
{
  "username": "user@example.com",
  "password": "password123"
}
```

#### RegisterRequest
```java
{
  "username": "newuser@example.com",
  "password": "securePassword123",
  "fullName": "John Doe",
  "number": "+1234567890"
}
```

### Response DTOs

#### AuthResponse
```java
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "errorCode": null
}
```

---

## 11. Error Handling

### Exception Handling Strategy

- **IllegalArgumentException**: Validation errors (400 Bad Request)
- **BadCredentialsException**: Authentication failure (401 Unauthorized)
- **AccessDeniedException**: Authorization failure (403 Forbidden)
- **EntityNotFoundException**: Resource not found (404 Not Found)
- **Generic Exception**: Server errors (500 Internal Server Error)

### Error Response Format
```json
{
  "error": "Error message",
  "code": "ERROR_CODE",
  "timestamp": "2024-03-31T10:30:00Z"
}
```

---

## 12. Performance Considerations

### Optimization Strategies

1. **Database Indexing**: Indexes on frequently queried fields
2. **Lazy Loading**: Use lazy loading for one-to-many relationships
3. **Connection Pooling**: HikariCP with appropriate pool size
4. **Caching**: Implement caching for categories and frequently accessed products
5. **Query Optimization**: Use custom JPA queries for complex operations
6. **Pagination**: Implement pagination for list endpoints

### Monitoring

- Monitor database connection pool usage
- Track JWT token generation/validation performance
- Monitor HTTP response times
- Set up alerts for error rates

---

## 13. Future Enhancements

1. **API Documentation**: Implement Swagger/OpenAPI
2. **Email Notifications**: Send confirmation emails for orders
3. **Payment Integration**: Stripe or PayPal integration
4. **Inventory Management**: Track stock levels
5. **Advanced Caching**: Redis integration
6. **Real-time Updates**: WebSocket support for notifications
7. **API Rate Limiting**: Prevent abuse
8. **Two-Factor Authentication**: Enhanced security
9. **File Upload Service**: S3 integration for product images
10. **Audit Logging**: Track all user actions

---

## Document Metadata

- **Last Updated**: March 31, 2026
- **Version**: 2.0
- **Author**: Development Team
- **Status**: Active
    end

    B -- jwt cookie --> F
    F --> U
    F --> J
    F --> W
```

### 2.2 Domain Relationship Diagram

```mermaid
erDiagram
    ACCOUNT ||--o{ CART : owns
    ACCOUNT ||--o{ ADDRESS : has
    ACCOUNT ||--o{ ORDER : places
    CATEGORY ||--o{ PRODUCT : groups
    CART ||--o{ CART_PRODUCT : contains
    PRODUCT ||--o{ CART_PRODUCT : references
    ORDER ||--o{ CART_PRODUCT : includes
    ADDRESS ||--o{ ORDER : used_for
```

## 3. Technology Stack

From `build.gradle` and configuration files:
- Java 17 (`toolchain`)
- Spring Boot 4.0.3
- Spring Data JPA + Hibernate
- Spring Security
- Spring MVC + Thymeleaf
- PostgreSQL driver (runtime)
- JWT via `io.jsonwebtoken` (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- Lombok
- Gradle build system
- Test DB: H2 (test scope)

## 4. Project Structure and Responsibilities

Root-level design-relevant files:
- `build.gradle`: dependencies and plugins
- `settings.gradle`: root project naming
- `src/main/resources/application.properties`: default profile + datasource behavior
- `src/main/resources/application-supabase.properties`: Supabase datasource/JPA/JWT properties
- `.env.supabase.example`: expected externalized environment variables

Code package structure under `src/main/java/com/me/kaffe`:
- `configuration.security`: security filter chain and auth wiring
- `configuration.security.impl`: JWT filter and `UserDetailsService` implementation
- `controller`: REST and web controllers
- `dto`: request DTO base + auth request payload classes
- `entity`: domain model and ORM mappings
- `repository`: JPA repositories
- `security`: JWT utility
- `service`: business/service layer for entities

Template structure under `src/main/resources/templates`:
- Pages: `home.html`, `menu.html`, `about.html`, `shipment.html`, `login.html`, `register.html`
- Shared fragments: `fragments/head.html`, `fragments/navbar.html`

## 5. Runtime Architecture

### 5.1 Logical Components

1) Browser client
- Accesses web pages (`/`, `/menu`, `/about`, `/shipment`, `/login`, `/register`)
- Sends form posts for login/register
- Stores JWT in `jwt` HTTP-only cookie

2) Spring MVC/REST layer
- `WebController` serves static-rendered page routes
- `AuthController` handles login/logout/register flows
- Resource controllers expose CRUD APIs under `/api/**`

3) Security layer
- `SecurityConfiguration` defines route protection and stateless session policy
- `JwtAuthenticationFilter` extracts JWT from cookie, validates it, and sets authentication context
- `UserDetailsServiceImpl` maps `Account` to Spring Security `UserDetails`

4) Service + repository layer
- Services currently implement direct CRUD delegation
- Repositories provide persistence via Spring Data JPA

5) Database
- PostgreSQL (Supabase intended by default profile)
- Hibernate generates/updates schema based on JPA mappings (`ddl-auto` configurable)

### 5.2 Request Flow (Authenticated API)

1. Client requests `/api/...` with `jwt` cookie.
2. `JwtAuthenticationFilter` runs before `UsernamePasswordAuthenticationFilter`.
3. Filter extracts token from cookie and resolves username via `JwtUtil`.
4. `UserDetailsServiceImpl` loads account by username.
5. If valid, Spring Security context is populated.
6. Controller executes and returns JSON response.

### 5.3 Authentication Flow

Login (`POST /login` in `AuthController`):
1. Credentials are authenticated by `AuthenticationManager`.
2. JWT is generated by `JwtUtil`.
3. JWT stored in HTTP-only `jwt` cookie (`path=/`, max age 1 day).
4. User is redirected to `/`.

Register (`POST /register`):
1. Checks username uniqueness via `AccountRepository.findByUsername`.
2. Persists `Account` with encoded password and role `CUSTOMER`.
3. Auto-issues JWT cookie and redirects to `/`.

Logout (`GET /logout`):
1. Sends cleared `jwt` cookie (`maxAge=0`).
2. Redirects to `/login?logout`.

## 6. Data Model and Persistence

Entities are UUID-based (`GenerationType.UUID`) and mapped to relational tables.

### 6.1 Entity Inventory

- `Account` (`account`)
  - Fields: `username`, `password`, `fullName`, `number`, `role`
  - Relations: one-to-many carts, addresses, orders

- `Address` (`address`)
  - Fields: `isDefault`, `city`, `details`
  - Relations: many-to-one account, one-to-many orders

- `Cart` (`cart`)
  - Relations: many-to-one account, one-to-many cartProducts

- `CartProduct` (`cart_product`)
  - Fields: `quantity`
  - Relations: many-to-one cart, product, order

- `Category` (`category`)
  - Fields: `name`, `description`
  - Relations: one-to-many products

- `Product` (`product`)
  - Fields: `name`, `sku`, `size`, `image`, `description`
  - Relations: many-to-one category, one-to-many cartProducts

- `Order` (`"order"`)
  - Fields: `status`
  - Relations: many-to-one account/address, one-to-many cartProducts

- `Role` enum: `CUSTOMER`, `ADMIN`, `EMPLOYEE`

### 6.2 Relationship Summary

- `Account` 1..* `Cart`
- `Account` 1..* `Address`
- `Account` 1..* `Order`
- `Category` 1..* `Product`
- `Cart` 1..* `CartProduct`
- `Product` 1..* `CartProduct`
- `Order` 1..* `CartProduct`
- `Address` 1..* `Order`

### 6.3 Repository Design

Each aggregate has a dedicated repository extending `JpaRepository<..., UUID>`.
- Custom query currently present only in `AccountRepository` (`findByUsername`).

## 7. API and Web Surface

### 7.1 Page Routes (Thymeleaf)

Served by `WebController` and `AuthController`:
- `GET /`
- `GET /menu`
- `GET /about`
- `GET /shipment`
- `GET /login`, `POST /login`
- `GET /register`, `POST /register`
- `GET /logout`

### 7.2 REST Endpoints

CRUD resources (all under `/api`):
- `/api/accounts`
- `/api/addresses`
- `/api/carts`
- `/api/cart-products`
- `/api/categories`
- `/api/orders`
- `/api/products`

Each resource controller provides:
- `GET /`
- `GET /{id}`
- `POST /`
- `PUT /{id}`
- `DELETE /{id}`

## 8. Security Architecture

### 8.1 Route Policy

Defined in `SecurityConfiguration`:
- Public: page routes (`/`, `/menu`, `/about`, `/shipment`, `/login`, `/register`) and static path patterns (`/css/**`, `/js/**`, `/images/**`)
- Protected: `/api/**` requires authentication
- Session policy: `STATELESS`
- CSRF: disabled
- Form login/logout filters: disabled (custom controller-based flow used)

### 8.2 JWT Strategy

- Token contains subject (`username`), issue time, expiration
- Secret and expiration are externalized properties (`jwt.secret`, `jwt.expiration-ms`)
- JWT transport via HTTP-only cookie named `jwt`

### 8.3 User Principal Mapping

`UserDetailsServiceImpl` maps persisted account role to authorities:
- `ROLE_CUSTOMER`, `ROLE_ADMIN`, `ROLE_EMPLOYEE`

## 9. Configuration and Environments

### 9.1 Default Profile Behavior

From `application.properties`:
- `spring.profiles.default=supabase`
- `spring.datasource.embedded-database-connection=none`

This enforces non-embedded DB usage by default and activates Supabase profile properties unless overridden.

### 9.2 Supabase Profile

From `application-supabase.properties`:
- Optional import of `.env.supabase`
- JDBC URL from `DATABASE_URL` or host/port/db fallbacks
- PostgreSQL driver and dialect set explicitly
- Hikari pool size configurable
- JPA `ddl-auto` configurable (default `update`)

Expected env contract documented in `.env.supabase.example`.

### 9.3 Test Configuration

`src/test/resources/application.properties` uses H2 in-memory DB with PostgreSQL mode and `create-drop` lifecycle for tests.

## 10. Frontend Template Architecture

- Thymeleaf pages are mostly static content currently (marketing + auth screens)
- Shared layout components:
  - `fragments/head.html`: title and Tailwind CDN include
  - `fragments/navbar.html`: navigation + auth visibility via Spring Security tags
- Auth pages (`login.html`, `register.html`) post to MVC auth endpoints
- Main pages do not yet consume API-backed dynamic product/catalog data

## 11. Testing and Quality Status

Current automated test coverage is minimal:
- `KaffeApplicationTests.contextLoads()` only verifies context startup under test profile

No dedicated tests found for:
- Controller behavior
- Security rules and JWT filter behavior
- Repository integration queries beyond defaults
- Service-level business logic

## 12. Architectural Strengths

- Clear and conventional Spring layering simplifies onboarding
- Clean separation between web pages and API endpoints
- Stateless JWT auth implementation is straightforward
- Supabase profile and env-based configuration support deployment portability
- UUID identifiers avoid sequence coordination issues across environments

## 13. Risks and Design Gaps

1) Entity exposure directly in REST APIs
- Controllers accept/return JPA entities, increasing coupling and risk of over-posting/serialization issues.

2) Limited validation and DTO usage
- DTO classes exist (`LoginRequest`, `RegisterRequest`) but are not used in controller methods, and validation is not enforced.

3) Missing role-based authorization
- APIs are authenticated but not role-segmented (`ADMIN` vs `CUSTOMER` operations not differentiated).

4) CSRF disabled with cookie-based auth
- For browser cookie auth, CSRF strategy should be intentionally reviewed.

5) Schema management in runtime
- `ddl-auto=update` is convenient for development but can be risky in production without migration tooling.

6) Limited test coverage
- Only context-load test currently; regression risk increases as features grow.

## 14. Recommended Evolution Roadmap

Priority 1:
- Introduce request/response DTOs for resource APIs and auth endpoints.
- Add bean validation (`jakarta.validation`) and centralized exception handling.
- Add integration tests for auth flow and protected API access.

Priority 2:
- Add fine-grained authorization with role-based endpoint constraints.
- Add migration tool (Flyway or Liquibase) and move away from runtime schema mutation in production.
- Add API pagination/filtering for list endpoints.

Priority 3:
- Connect Thymeleaf menu/catalog pages to real product/category data.
- Add audit fields (created/updated timestamps) and basic observability (request logging, health metrics).

## 15. Source Coverage Matrix

This document is derived from:
- Build/config: `build.gradle`, `settings.gradle`, `README.md`, `HELP.md`, `.env.supabase.example`, `.env.supabase`, `src/main/resources/application.properties`, `src/main/resources/application-supabase.properties`, `src/test/resources/application.properties`
- App/security: `src/main/java/com/me/kaffe/KaffeApplication.java`, `src/main/java/com/me/kaffe/configuration/security/SecurityConfiguration.java`, `src/main/java/com/me/kaffe/configuration/security/impl/JwtAuthenticationFilter.java`, `src/main/java/com/me/kaffe/configuration/security/impl/UserDetailsServiceImpl.java`, `src/main/java/com/me/kaffe/security/JwtUtil.java`
- Domain/persistence: all files under `src/main/java/com/me/kaffe/entity`, `src/main/java/com/me/kaffe/repository`, `src/main/java/com/me/kaffe/service`
- Controllers: all files under `src/main/java/com/me/kaffe/controller`
- DTOs: all files under `src/main/java/com/me/kaffe/dto`
- Templates: all files under `src/main/resources/templates`
- Tests: `src/test/java/com/me/kaffe/KaffeApplicationTests.java`

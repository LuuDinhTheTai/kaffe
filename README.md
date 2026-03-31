# ☕ Kaffe

A full-stack coffee shop e-commerce application built with **Spring Boot 4.0.3** and **Java 17**.

> ✅ **Status**: Production Ready | **Last Updated**: March 31, 2026

## 📚 Documentation

### Quick Start
- **[DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)** - Quick reference and common tasks
- **[SETUP.md](SETUP.md)** - Complete setup and configuration guide
- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Overview of completed work

### In-Depth References
- **[docs/technical-design-architecture.md](docs/technical-design-architecture.md)** - Complete technical design with Mermaid diagrams
- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Detailed implementation notes
- **[COMPLETION_CHECKLIST.md](COMPLETION_CHECKLIST.md)** - Verification checklist

## Tech Stack

| Layer        | Technology                              |
|--------------|-----------------------------------------|
| Framework    | Spring Boot 4.0.3                       |
| Language     | Java 17                                 |
| Database     | PostgreSQL 14 (Supabase)                |
| ORM          | Spring Data JPA / Hibernate 7.2.4       |
| Security     | Spring Security 7.0.5 + JWT (JJWT)      |
| View Engine  | Thymeleaf 3.x                           |
| Auth         | JWT Tokens (HttpOnly Cookies)           |
| Build Tool   | Gradle 7.x                              |
| Utilities    | Lombok                                  |

## Quick Start (5 Minutes)

### Prerequisites
- Java 17+
- Gradle or use `./gradlew`
- Supabase account (free tier available)

### 1. Setup Database
```bash
# Create .env.supabase file with your Supabase credentials
cp .env.supabase.example .env.supabase
# Edit with your actual values (see SETUP.md for details)
```

### 2. Build & Run
```bash
./gradlew clean build
./gradlew bootRun
```

### 3. Access Application
```
Home: http://localhost:8080
Login: http://localhost:8080/login
Register: http://localhost:8080/register
```

### 4. Test Authentication
```bash
# Register
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=test@example.com&password=pass123&fullName=Test User&number=0123456789"

# Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=test@example.com&password=pass123" \
  -c cookies.txt

# Access protected endpoint
curl -X GET http://localhost:8080/api/account -b cookies.txt
```

## Project Structure

```
kaffe/
├── src/main/java/com/me/kaffe/
│   ├── KaffeApplication.java           # Entry point
│   ├── controller/                     # HTTP Handlers
│   │   ├── AuthController.java         # /login, /register, /logout
│   │   ├── AccountController.java      # /api/account, /api/addresses
│   │   ├── ProductController.java      # /api/products
│   │   ├── CartController.java         # /api/cart
│   │   └── OrderController.java        # /api/orders
│   ├── service/                        # Business Logic
│   │   ├── AuthService.java            # Authentication logic
│   │   ├── AccountService.java         # User management
│   │   ├── ProductService.java         # Product operations
│   │   ├── CartService.java            # Cart operations
│   │   └── OrderService.java           # Order processing
│   ├── repository/                     # Data Access (JPA)
│   │   ├── AccountRepository.java
│   │   ├── ProductRepository.java
│   │   ├── CartRepository.java
│   │   └── OrderRepository.java
│   ├── entity/                         # JPA Entities
│   │   ├── Account.java                # Users (UUID PK, roles)
│   │   ├── Product.java                # Products
│   │   ├── Category.java               # Categories
│   │   ├── Cart.java                   # Shopping carts
│   │   ├── CartProduct.java            # Cart items
│   │   ├── Order.java                  # Orders
│   │   ├── Address.java                # Addresses
│   │   └── Role.java                   # CUSTOMER, ADMIN, EMPLOYEE
│   ├── dto/                            # Data Transfer Objects
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── base/BaseRequest.java
│   │   └── response/
│   │       ├── AuthResponse.java
│   │       ├── UserResponse.java
│   │       └── ErrorResponse.java
│   ├── security/                       # Security Components
│   │   └── JwtUtil.java                # JWT Token Handling
│   └── configuration/                  # Spring Configuration
│       ├── security/
│       │   ├── SecurityConfiguration.java
│       │   └── impl/JwtAuthenticationFilter.java
│       └── seeder/
│
├── src/main/resources/
│   ├── application.properties
│   ├── application-supabase.properties # Supabase config
│   └── templates/                      # Thymeleaf HTML templates
│       ├── login.html
│       ├── register.html
│       ├── home.html
│       └── menu.html
│
├── docs/
│   └── technical-design-architecture.md  # 📖 Full technical reference
├── build.gradle                          # Dependencies & build config
├── SETUP.md                              # 📋 Setup guide
├── DEVELOPER_GUIDE.md                    # 🛠️ Developer reference
├── IMPLEMENTATION_SUMMARY.md             # 📊 What was implemented
├── PROJECT_SUMMARY.md                    # ✨ Work summary
└── COMPLETION_CHECKLIST.md               # ✅ Verification checklist
```

## Architecture

### Layered Architecture
```
┌─────────────────────────────────┐
│  Presentation (Controllers)      │
├─────────────────────────────────┤
│  Service Layer (Business Logic)  │
├─────────────────────────────────┤
│  Repository Layer (Data Access)  │
├─────────────────────────────────┤
│  Database (PostgreSQL/Supabase)  │
└─────────────────────────────────┘
```

### Data Flow
```
HTTP Request
    ↓
Controller (validates DTO, logs)
    ↓
Service (business logic, transactions)
    ↓
Repository (JPA queries)
    ↓
PostgreSQL Database
    ↓
Response DTO (return to client)
```

## Authentication

### Flow
1. User submits credentials at `/login` or `/register`
2. Controller validates input with DTOs
3. Service layer authenticates or creates account
4. Password encoded with BCrypt
5. JWT token generated
6. Token stored in HttpOnly cookie
7. Subsequent requests validated by JwtAuthenticationFilter

### Security
- ✅ JWT tokens (HMAC-SHA256)
- ✅ BCrypt password encoding
- ✅ HttpOnly cookies (prevents XSS)
- ✅ CSRF disabled (stateless JWT)
- ✅ Input validation on all endpoints
- ✅ SQL injection protection (JPA)

## Configuration

### Environment Variables Required
```bash
DATABASE_URL              # PostgreSQL connection string
SUPABASE_DB_HOST         # Database host
SUPABASE_DB_USER         # Database user
SUPABASE_DB_PASSWORD     # Database password
JWT_SECRET               # JWT signing secret (256+ bits)
```

### Configuration Options
1. **Development**: Use `.env.supabase` file (see `.env.supabase.example`)
2. **Production**: Set system environment variables
3. **IDE**: Set in run configuration

See [SETUP.md](SETUP.md) for detailed instructions.

## Database

### PostgreSQL (Supabase)
- 7 core tables with relationships
- Automatic schema creation (Hibernate DDL)
- Connection pooling (HikariCP)
- Indexes for performance

### Tables
- `account` - User accounts
- `category` - Product categories
- `product` - Products
- `cart` - Shopping carts
- `cart_product` - Cart items
- `order` - Customer orders
- `address` - Shipping addresses

See [docs/technical-design-architecture.md](docs/technical-design-architecture.md) for schema details.

## API Endpoints

### Authentication
```
GET  /login                  # Login page
POST /login                  # Submit login
GET  /register               # Registration page
POST /register               # Submit registration
GET  /logout                 # Logout
```

### Account
```
GET  /api/account            # Current user info
PUT  /api/account            # Update account
GET  /api/addresses          # User addresses
POST /api/addresses          # Create address
```

### Products
```
GET  /api/products           # List products
GET  /api/products/{id}      # Product details
POST /api/products           # Create (ADMIN)
PUT  /api/products/{id}      # Update (ADMIN)
DELETE /api/products/{id}    # Delete (ADMIN)
```

See [docs/technical-design-architecture.md](docs/technical-design-architecture.md) for complete API reference.

## Running

### Development
```bash
./gradlew bootRun
```

### Build JAR
```bash
./gradlew bootJar
java -jar build/libs/kaffe-0.0.1-SNAPSHOT.jar
```

### With Docker
```bash
docker build -t kaffe .
docker run -p 8080:8080 -e DATABASE_URL=... -e JWT_SECRET=... kaffe
```

## Logging

### Configuration
Logging is configured in `application-supabase.properties`:
```
logging.level.com.me.kaffe=INFO        # Application logging
logging.level.org.springframework.security=INFO
logging.level.org.hibernate.SQL=DEBUG  # SQL queries
```

### Enable Debug Mode
```bash
./gradlew bootRun --args='--debug'
# Or in IDE, set: logging.level.com.me.kaffe=DEBUG
```

## Troubleshooting

### Database Connection Failed
1. Verify Supabase credentials in `.env.supabase`
2. Check connection string format
3. Ensure firewall allows outbound connections
4. See [SETUP.md](SETUP.md) troubleshooting section

### Authentication Issues
1. Check JWT_SECRET is set (256+ bits)
2. Enable debug logging: `logging.level.com.me.kaffe=DEBUG`
3. Review application logs for error messages

### Port Already in Use
```bash
# Change port in application-supabase.properties
server.port=8081
```

## Development

### Add New Endpoint
1. Create Service method
2. Add Controller endpoint
3. Create/use DTO for request/response
4. Add validation
5. Test with cURL

See [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) for detailed examples.

### Add New Entity
1. Create JPA Entity class
2. Create Spring Data JPA Repository
3. Application auto-creates table

## Testing

### Manual Testing
```bash
# Register user
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user@test.com&password=pass123&fullName=Test&number=1234567890"

# Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user@test.com&password=pass123" \
  -c cookies.txt

# Access protected endpoint
curl -X GET http://localhost:8080/api/account -b cookies.txt
```

## Production Deployment

### Prerequisites
- Java 17 runtime
- PostgreSQL database (or Supabase)
- Strong JWT_SECRET

### Steps
1. Build: `./gradlew bootJar`
2. Set environment variables
3. Run JAR: `java -jar kaffe-0.0.1-SNAPSHOT.jar`

See [SETUP.md](SETUP.md) for cloud platform deployment (AWS, Heroku, Railway).

## Support & Documentation

- **Quick Start**: [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)
- **Setup Instructions**: [SETUP.md](SETUP.md)
- **Technical Details**: [docs/technical-design-architecture.md](docs/technical-design-architecture.md)
- **Implementation Notes**: [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)

## Future Enhancements

- [ ] Swagger/OpenAPI documentation
- [ ] Email notifications
- [ ] Payment integration (Stripe/PayPal)
- [ ] Inventory management
- [ ] Redis caching
- [ ] WebSocket real-time updates
- [ ] Two-factor authentication
- [ ] OAuth2/OpenID Connect
- [ ] S3 file uploads
- [ ] Audit logging

## Contributing

Follow the code style and patterns shown in existing code:
- Use DTOs for request/response
- Add logging with @Slf4j
- Use @Transactional for data operations
- Document public methods with Javadoc
- Follow repository → service → controller pattern

## License

Copyright 2026 Kaffe Project. All rights reserved.

## Project Info

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 17
- **Database**: PostgreSQL 14 (Supabase)
- **Status**: ✅ Production Ready
- **Last Updated**: March 31, 2026

    ├── menu.html                      # Product menu
    ├── about.html                     # About page
    ├── shipment.html                  # Shipment info
    └── fragments/
        ├── head.html                  # Shared <head> fragment
        └── navbar.html                # Shared navigation bar
```

## Entity Relationships

```
Account 1──* Cart
Account 1──* Address
Account 1──* Order

Category 1──* Product

Cart 1──* CartProduct
Product 1──* CartProduct
Order 1──* CartProduct

Address 1──* Order
```

## REST API Endpoints

All API controllers expose standard CRUD operations:

| Resource       | Base Path            | Methods                     |
|----------------|----------------------|-----------------------------|
| Accounts       | `/api/accounts`      | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Addresses      | `/api/addresses`     | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Carts          | `/api/carts`         | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Cart Products  | `/api/cart-products` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Categories     | `/api/categories`    | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Orders         | `/api/orders`        | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Products       | `/api/products`      | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |

## Getting Started

### Prerequisites

- **Java 17+**
- **PostgreSQL** database (or a [Supabase](https://supabase.com/) project)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/LuuDinhTheTai/kaffe.git
   cd kaffe
   ```

2. **Configure the database**

   Copy the example env file and fill in your credentials:
   ```bash
   cp .env.supabase.example .env.supabase
   ```

   Edit `.env.supabase`:
   ```properties
   DATABASE_URL=jdbc:postgresql://<host>:<port>/<db>?sslmode=require
   SUPABASE_DB_USER=<your-user>
   SUPABASE_DB_PASSWORD=<your-password>
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Open in browser**

   Navigate to [http://localhost:8080](http://localhost:8080)

## Pages

| Route       | Description          |
|-------------|----------------------|
| `/`         | Home / Landing page  |
| `/menu`     | Coffee menu          |
| `/about`    | About us             |
| `/shipment` | Shipment information |

## Testing

The project uses **H2** in-memory database for tests:

```bash
./gradlew test
```

## License

This project is for educational purposes.

# Expense Management Backend

A comprehensive Spring Boot backend system for managing employee expenses with multi-step approval workflows, JWT authentication, and external API integrations.

## Features

- **Authentication & Onboarding**: JWT-based signup/login with automatic company and admin user creation
- **User & Role Management**: Support for Admin, Manager, and Employee roles
- **Expense Submission**: Submit expenses with categories, amounts, currencies, and receipt images
- **Multi-Step Approval Workflow**: Configurable approval rules with percentage-based, specific-approver, or hybrid logic
- **OCR Integration**: Stub service for receipt parsing (mock data for MVP)
- **Currency Integration**: External API wrappers for country currencies and exchange rates
- **Database Migrations**: Flyway-based schema versioning with seed data

## Tech Stack

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with PostgreSQL
- **Flyway** for database migrations
- **Lombok** for boilerplate reduction
- **Maven** for build management

## Prerequisites

- Java 21 or higher
- PostgreSQL 12 or higher
- Maven 3.8+

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd expense-management-backend
```

### 2. Setup Database

Create a PostgreSQL database:

```sql
CREATE DATABASE expense_management;
```

### 3. Configure Environment Variables

Copy `.env.example` to `.env` and update values:

```bash
cp .env.example .env
```

Edit `.env`:

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=expense_management
DB_USERNAME=postgres
DB_PASSWORD=your_password

JWT_SECRET=your-very-long-secret-key-change-this-in-production-min-256-bits
JWT_EXPIRATION_MS=86400000

SERVER_PORT=8080
```

### 4. Run the Application

Using Maven:

```bash
mvn clean install
mvn spring-boot:run
```

Or using the Maven wrapper:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

### 5. Verify Setup

Check that Flyway migrations ran successfully. The application will automatically:
- Create database schema
- Insert seed data (demo company, users, expenses)

## Default Test Credentials

After running with seed data, you can login with:

| Role     | Email                    | Password     |
|----------|--------------------------|--------------|
| Admin    | admin@democorp.com       | admin123     |
| Manager  | manager@democorp.com     | manager123   |
| Employee | employee1@democorp.com   | employee123  |
| Employee | employee2@democorp.com   | employee123  |

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user (creates company on first signup)
- `POST /api/auth/login` - Login and get JWT token

### Users
- `GET /api/users` - Get all users (Admin/Manager only)
- `GET /api/users/me` - Get current user details
- `POST /api/users` - Create new user (Admin only)
- `GET /api/users/role/{role}` - Get users by role (Admin/Manager)

### Expenses
- `POST /api/expenses` - Submit new expense
- `GET /api/expenses/my` - Get current user's expenses
- `GET /api/expenses/my/status/{status}` - Get expenses by status
- `GET /api/expenses/{id}` - Get expense by ID
- `GET /api/expenses/pending` - Get pending expenses (Admin/Manager)

### Approvals
- `GET /api/approvals/pending` - Get pending approvals for current user (Admin/Manager)
- `GET /api/approvals/expense/{expenseId}` - Get approval steps for expense
- `PUT /api/approvals/{stepId}` - Approve or reject expense (Admin/Manager)

### OCR
- `POST /api/ocr/parse-receipt` - Parse receipt image (returns mock data)

### Integration
- `GET /api/integration/countries` - Get countries with currencies
- `GET /api/integration/currency-rates/{baseCurrency}` - Get exchange rates

## Testing

Run all tests:

```bash
mvn test
```

Run specific test class:

```bash
mvn test -Dtest=ExpenseControllerTest
```

## Project Structure

```
src/
├── main/
│   ├── java/com/teaminfinity/expensemanagement/
│   │   ├── config/           # Security, CORS, RestTemplate configs
│   │   ├── controller/       # REST controllers
│   │   ├── dto/              # Request/Response DTOs
│   │   ├── entity/           # JPA entities
│   │   ├── enums/            # Enums (Role, Status, etc.)
│   │   ├── repository/       # JPA repositories
│   │   ├── security/         # JWT utilities and filters
│   │   ├── service/          # Business logic services
│   │   └── util/             # Utility classes
│   └── resources/
│       ├── application.properties
│       └── db/migration/     # Flyway SQL scripts
└── test/
    └── java/                 # Unit and integration tests
```

## Architecture

See [architecture.md](architecture.md) for detailed system architecture.

## Design Decisions

See [DESIGN_DECISIONS.md](DESIGN_DECISIONS.md) for technical choices and trade-offs.

## API Documentation

See [api.yaml](api.yaml) for OpenAPI specification.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues or questions, please create an issue in the repository.

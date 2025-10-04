# Design Decisions

This document outlines key architectural and technical decisions made in the Expense Management Backend system.

## 1. Authentication & Authorization

### Decision: JWT-based Stateless Authentication
**Rationale**: 
- JWT tokens enable stateless authentication, improving scalability
- No server-side session storage required
- Easy integration with frontend applications
- Standard industry practice for REST APIs

**Trade-offs**:
- Cannot immediately revoke tokens (must wait for expiration)
- Larger request payload compared to session IDs
- **Mitigation**: Short expiration times (24 hours) and refresh token pattern for production

### Decision: Role-Based Access Control (RBAC)
**Rationale**:
- Simple three-role model (Admin, Manager, Employee) covers most use cases
- Spring Security's `@PreAuthorize` provides declarative security
- Clear separation of permissions

**Alternative Considered**: Attribute-Based Access Control (ABAC)
- Rejected for MVP due to complexity; can be added later if needed

## 2. Database Design

### Decision: PostgreSQL
**Rationale**:
- ACID compliance for financial data
- Rich data types (DECIMAL for monetary values)
- Excellent JSON support for future extensibility
- Wide adoption and tooling support

### Decision: Flyway for Migrations
**Rationale**:
- Version control for database schema
- Repeatable deployments across environments
- Easy rollback capabilities
- Integrates seamlessly with Spring Boot

### Decision: Soft Deletes NOT Implemented
**Rationale**:
- MVP scope - foreign key cascades handle deletions
- Can be added in v2 if audit trail is required

## 3. Approval Workflow Design

### Decision: Multi-Step Approval with Configurable Rules
**Rationale**:
- Flexible enough to handle various organizational structures
- Supports percentage-based, specific-approver, and hybrid rules
- Sequence-based ordering ensures proper approval flow

**Key Features**:
- `IS_MANAGER_APPROVER` flag: Manager can be first approver
- `ApprovalEvaluator` utility: Centralizes rule evaluation logic
- `CompanyApprovalRule` entity: Company-specific configuration

**Trade-offs**:
- More complex than simple "manager approves all" model
- Requires careful rule configuration
- **Mitigation**: Clear documentation and sensible defaults

### Decision: Approval Steps Are Immutable After Creation
**Rationale**:
- Audit trail: Cannot change who was supposed to approve
- Simplifies concurrency handling
- Clear workflow state

**Alternative Considered**: Dynamic re-evaluation on expense changes
- Rejected for MVP to avoid complexity

## 4. Currency & Integration

### Decision: External API with Fallback Mock Data
**Rationale**:
- REST Countries API: Free, no auth required, reliable
- Exchange Rate API: Free tier available, easy integration
- Mock fallback: Ensures system works offline or if APIs are down

**Implementation**:
- `ENABLE_EXTERNAL_API` flag: Toggle external calls
- RestTemplate: Simple HTTP client, good for MVP
- **Future**: Consider WebClient (reactive) or API caching

### Decision: No Currency Conversion in Expense Submission
**Rationale**:
- MVP scope: Store expenses in original currency
- Conversion can be done at reporting time
- Avoids complexity of locking exchange rates at submission time

## 5. OCR Service

### Decision: Stub Implementation with Mock Data
**Rationale**:
- Real OCR integration (Azure Computer Vision, AWS Textract) requires external accounts
- Mock data allows testing full workflow
- Easy to swap with real implementation later

**Interface Design**:
- Accepts `MultipartFile` (standard Spring file upload)
- Returns structured `OcrResult` (amount, date, vendor, category, confidence)
- **Production Path**: Uncomment integration code, add API keys

## 6. Security Configuration

### Decision: CSRF Disabled for JWT Authentication
**Rationale**:
- Stateless JWT authentication doesn't use cookies
- CSRF protection not needed for Bearer token auth
- Standard practice for REST APIs

### Decision: Password Encoding with BCrypt
**Rationale**:
- Industry standard for password hashing
- Adaptive cost factor (default 10 rounds)
- Built into Spring Security

**Security Considerations**:
- Minimum password length: 6 characters (configurable)
- No password complexity requirements in MVP (add in production)

## 7. Error Handling

### Decision: Runtime Exceptions for Business Logic Errors
**Rationale**:
- Simple for MVP
- Spring's `@ControllerAdvice` can be added for global error handling
- HTTP status codes handled by Spring Security

**Future Enhancement**:
- Custom exception hierarchy (e.g., `ResourceNotFoundException`, `UnauthorizedException`)
- Standardized error response format
- Logging and monitoring integration

## 8. Testing Strategy

### Decision: Integration Tests for Controllers, Unit Tests for Utilities
**Rationale**:
- Controller tests: Verify HTTP layer, security, and serialization
- Utility tests: Fast, isolated, deterministic
- `@SpringBootTest`: Full Spring context for realistic testing

**Test Data**:
- `@MockBean` for service layer: Faster tests, isolated concerns
- Seed data in Flyway: Manual testing and local development

## 9. Package Structure

### Decision: Layer-Based Package Organization
**Structure**:
```
controller/ - REST endpoints
service/    - Business logic
repository/ - Data access
entity/     - JPA entities
dto/        - Request/response objects
config/     - Spring configuration
security/   - JWT and auth filters
util/       - Shared utilities
```

**Rationale**:
- Clear separation of concerns
- Easy navigation for developers
- Standard Spring Boot convention

**Alternative Considered**: Feature-based (e.g., `expense/`, `approval/`)
- Better for microservices; overkill for monolith MVP

## 10. API Design

### Decision: RESTful Resource-Oriented URLs
**Rationale**:
- `/api/expenses`, `/api/approvals`, `/api/users`
- Standard HTTP methods (GET, POST, PUT, DELETE)
- Easy to document with OpenAPI

### Decision: JWT in Authorization Header
**Format**: `Authorization: Bearer <token>`
**Rationale**:
- Standard OAuth 2.0 / OpenID Connect pattern
- Supported by all HTTP clients
- Avoids CORS issues with custom headers

## 11. Assumptions & Simplifications

1. **Single Company per Deployment**: Each instance assumes one company. Multi-tenancy can be added later.
   - **Current**: Company ID filtering in queries
   - **Future**: Add tenant isolation, separate schemas

2. **No Email Notifications**: Approval/rejection notifications are manual.
   - **Future**: Integrate SMTP or email service (SendGrid, SES)

3. **No File Storage**: Receipt images are URLs (not uploaded files).
   - **Current**: Frontend handles file upload to cloud storage (S3, Azure Blob)
   - **Future**: Add file upload endpoint with cloud storage integration

4. **No Audit Log**: Changes are not tracked in detail.
   - **Future**: Add `@EntityListeners` for JPA auditing

5. **No Pagination**: All list endpoints return full results.
   - **Future**: Add Spring Data's `Pageable` for large datasets

6. **No Rate Limiting**: API is unprotected from abuse.
   - **Future**: Add Spring Cloud Gateway or Bucket4j

## 12. Production Readiness Checklist

For production deployment, consider:

- [ ] Replace default JWT secret with strong random key (256+ bits)
- [ ] Enable HTTPS (TLS/SSL certificates)
- [ ] Add API rate limiting and throttling
- [ ] Implement global exception handler with standardized error responses
- [ ] Add structured logging (Logback with JSON format)
- [ ] Integrate monitoring (Prometheus, Grafana, Datadog)
- [ ] Add health check endpoints (`/actuator/health`)
- [ ] Configure connection pooling (HikariCP tuning)
- [ ] Add database backups and disaster recovery plan
- [ ] Implement password complexity requirements
- [ ] Add account lockout after failed login attempts
- [ ] Enable CORS only for trusted origins
- [ ] Add API documentation (Swagger UI or ReDoc)
- [ ] Implement refresh token mechanism for JWT
- [ ] Add integration with real OCR service
- [ ] Set up CI/CD pipeline (GitHub Actions, Jenkins)
- [ ] Add containerization (Docker, but removed per requirements)
- [ ] Configure environment-specific properties (dev, staging, prod)

## Summary

This design prioritizes:
1. **MVP Scope**: Core features working correctly
2. **Extensibility**: Easy to add features later (pagination, notifications, etc.)
3. **Security**: Industry-standard practices (JWT, BCrypt, RBAC)
4. **Clarity**: Simple, readable code over premature optimization
5. **Testability**: Key business logic is unit tested

Trade-offs favor simplicity and time-to-market while maintaining clean architecture for future enhancements.

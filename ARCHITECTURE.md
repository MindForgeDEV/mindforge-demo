# MindForge Architecture Documentation

## Table of Contents

- [Overview](#overview)
- [System Architecture](#system-architecture)
  - [High-Level Architecture](#high-level-architecture)
  - [Technology Stack](#technology-stack)
- [Backend Architecture](#backend-architecture)
  - [Layered Architecture Pattern](#layered-architecture-pattern)
  - [Package Structure](#package-structure)
  - [Design Patterns Implemented](#design-patterns-implemented)
  - [Security Architecture](#security-architecture)
  - [API Design & Implementation](#api-design--implementation)
  - [Data Architecture](#data-architecture)
- [Frontend Architecture](#frontend-architecture)
  - [Component Architecture](#component-architecture)
  - [State Management](#state-management)
  - [API Integration Layer](#api-integration-layer)
- [DevOps Architecture](#devops-architecture)
  - [Containerization Strategy](#containerization-strategy)
  - [Environment Management](#environment-management)
- [Quality Assurance](#quality-assurance)
  - [Testing Strategy](#testing-strategy)
  - [Code Quality Standards](#code-quality-standards)
- [Performance Considerations](#performance-considerations)
  - [Backend Performance](#backend-performance)
  - [Frontend Performance](#frontend-performance)
- [Scalability Considerations](#scalability-considerations)
  - [Horizontal Scaling](#horizontal-scaling)
  - [Vertical Scaling](#vertical-scaling)
- [Security Architecture](#security-architecture)
  - [Authentication & Authorization](#authentication--authorization)
  - [Data Protection](#data-protection)
- [Deployment Architecture](#deployment-architecture)
  - [CI/CD Pipeline](#cicd-pipeline-planned)
  - [Infrastructure as Code](#infrastructure-as-code)
- [Monitoring & Observability](#monitoring--observability)
  - [Application Metrics](#application-metrics)
  - [Logging Strategy](#logging-strategy)
- [Future Enhancements](#future-enhancements)
  - [Microservices Migration](#microservices-migration)
  - [Advanced Features](#advanced-features)
  - [Cloud-Native Evolution](#cloud-native-evolution)

## Overview

MindForge is a modern, enterprise-grade full-stack web application built with Java 21, Spring Boot 3, Vue 3, and TypeScript. The application demonstrates best practices in software architecture, clean code principles, and DevOps methodologies.

## System Architecture

### High-Level Architecture

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐
│   Vue 3 SPA     │◄──────────────► │ Spring Boot API │
│   (Frontend)    │                 │   (Backend)     │
└─────────────────┘                 └─────────────────┘
         │                                   │
         │                                   │
         ▼                                   ▼
┌─────────────────┐                 ┌─────────────────┐
│   Nginx Web     │                 │   PostgreSQL    │
│   Server        │                 │   Database      │
└─────────────────┘                 └─────────────────┘
```

### Technology Stack

#### Backend (Java/Spring Boot)
- **Framework**: Spring Boot 3.5.7
- **Language**: Java 21 (LTS)
- **Database**: PostgreSQL 16
- **ORM**: Spring Data JPA with Hibernate
- **Security**: JWT-based authentication
- **API Documentation**: OpenAPI 3.1 with Swagger UI
- **Build Tool**: Gradle with Kotlin DSL
- **Testing**: JUnit 5, Mockito, AssertJ

#### Frontend (Vue.js/TypeScript)
- **Framework**: Vue 3.5.22 with Composition API
- **Language**: TypeScript 5.9.3 with strict mode
- **Build Tool**: Vite with Rollup
- **HTTP Client**: Axios with interceptors
- **Testing**: Vitest, Vue Test Utils, jsdom
- **Styling**: Scoped CSS with Vue SFC

#### DevOps & Infrastructure
- **Containerization**: Docker & Docker Compose
- **Development Environment**: Nix Flakes
- **Process Management**: Podman (alternative to Docker)
- **Database Migrations**: Liquibase

## Backend Architecture

### Layered Architecture Pattern

The backend follows a clean, layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────┐
│         Controller Layer            │
│   (REST API Endpoints)              │
├─────────────────────────────────────┤
│         Service Layer               │
│   (Business Logic)                  │
├─────────────────────────────────────┤
│       Repository Layer              │
│   (Data Access)                     │
├─────────────────────────────────────┤
│         Entity Layer                │
│   (Domain Models)                   │
└─────────────────────────────────────┘
```

### Package Structure

```
mindforge/
├── config/           # Configuration classes
│   ├── ApplicationConfig.java  # Bean definitions
│   ├── JwtConfig.java          # JWT configuration
│   └── SecurityConfig.java     # Security configuration
├── controller/       # REST controllers
│   ├── AuthController.java     # Authentication endpoints
│   └── HealthController.java   # Health check endpoints
├── dto/             # Data Transfer Objects
│   ├── AuthenticationResponseDto.java # Authentication responses
│   ├── UserRequestDto.java     # User input data
│   └── UserResponseDto.java    # User output data
├── model/           # JPA entities
│   └── User.java               # User domain model
├── repository/      # Data access layer
│   └── UserRepository.java     # User data access
├── service/         # Business logic layer
│   ├── AuthenticationService.java # Authentication business logic
│   └── JwtService.java         # JWT token management
└── MindforgeApplication.java   # Main application class
```

### Design Patterns Implemented

#### 1. **Dependency Injection Pattern**
- Constructor-based injection using Spring's `@Autowired`
- Lombok's `@RequiredArgsConstructor` for clean constructors
- Interface-based programming for testability

#### 2. **Repository Pattern**
- Abstract data access with `JpaRepository`
- Custom query methods with Spring Data
- Separation of data access from business logic

#### 3. **Service Layer Pattern**
- Business logic encapsulation in service classes
- Transaction management with `@Transactional`
- Exception handling and validation

#### 4. **DTO Pattern**
- Separation of internal models from API contracts
- Input validation with Bean Validation
- Builder pattern for object construction

#### 5. **Configuration Management**
- Profile-based configuration (`dev`, `test`, `prod`)
- Externalized configuration with Spring Boot
- Environment-specific property files

### Security Architecture

#### JWT-Based Authentication
```
Client Request → JWT Token Validation → User Context → Business Logic
```

- **Token Generation**: Secure key-based signing with HS256
- **Token Validation**: Stateless verification on each request
- **User Context**: Automatic user resolution from JWT claims
- **Role-Based Access**: Support for USER and ADMIN roles

#### Security Configuration
- **CORS**: Configured for frontend-backend communication
- **CSRF**: Disabled for API-first architecture
- **Session Management**: Stateless with JWT tokens
- **Password Security**: BCrypt hashing with Spring Security

### API Design & Implementation

#### OpenAPI Specification Approach
The project uses a **hybrid approach** combining OpenAPI specification with code generation:

- **OpenAPI Spec**: Defines API contracts in `openapi.yaml`
- **Code Generation**: OpenAPI generator creates interfaces and models
- **Manual Implementation**: Controllers with custom business logic
- **Jakarta Compatibility**: Generated code uses `jakarta.*` for Spring Boot 3
- **Type Safety**: Frontend API client manually implemented for optimal control

#### API Generation Strategy
```
OpenAPI Spec (Contract Definition)
    ↓
OpenAPI Generator (Spring Boot 3 + Jakarta)
    ↓
Generated Interfaces + Models (Structure)
    ↓
Manual Controller Implementation (Business Logic)
    ↓
Enhanced DTOs (Validation + Methods)
    ↓
Type-Safe Frontend Client (Integration)
```

#### Generated API Components
- **API Interfaces**: Contract definitions with OpenAPI annotations
- **Data Models**: Comprehensive DTOs with validation and builders
- **Controller Templates**: Basic implementations ready for customization
- **Jakarta Compatibility**: Spring Boot 3 compatible imports and annotations
OpenAPI Spec (Contract) → Manual Implementation (Control)
     ↓
Generated Models (Structure) + Custom Business Logic (Behavior)
     ↓
Type-Safe Frontend Client (Integration)
```

**Why Manual Implementation?**
- **Control**: Full control over business logic and error handling
- **Integration**: Seamless integration with existing services
- **Flexibility**: Easy to modify and extend beyond generated code
- **Compatibility**: Avoids Spring Boot 3 compatibility issues with generated code

### Data Architecture

#### Entity-Relationship Model
```
User
├── id: Long (Primary Key)
├── username: String (Unique)
├── password: String (Hashed)
└── role: String (USER/ADMIN)
```

#### Database Design Principles
- **Normalized Schema**: Proper relationship modeling
- **Indexing**: Optimized queries with database indexes
- **Constraints**: Data integrity with unique constraints
- **Migrations**: Version-controlled schema changes with Liquibase

## Frontend Architecture

### Component Architecture

```
App.vue (Root Component)
├── LoginForm.vue        # Authentication form
├── RegisterForm.vue     # User registration
└── ProfileView.vue      # User profile display
```

### State Management

#### Reactive State with Vue 3 Composition API
- **Local Component State**: `ref()` for reactive variables
- **Form State**: Two-way binding with `v-model`
- **API State**: Loading states and error handling
- **Global State**: localStorage for JWT token persistence

### API Integration Layer

#### Centralized HTTP Client
```typescript
// src/api/auth.ts
export const authApi = {
  register: (userData) => axios.post('/auth/register', userData),
  login: (credentials) => axios.post('/auth/login', credentials),
  me: () => axios.get('/auth/me')
}
```

#### Request/Response Interceptors
- **Authentication**: Automatic JWT token injection
- **Error Handling**: Centralized error processing
- **Loading States**: Request/response state management

## DevOps Architecture

### Containerization Strategy

#### Multi-Stage Docker Builds
```dockerfile
# Backend Dockerfile
FROM eclipse-temurin:21-jdk AS builder
# Build JAR file

FROM eclipse-temurin:21-jre AS runtime
# Run application
```

#### Service Orchestration
```yaml
# docker-compose.yml
services:
  backend:    # Spring Boot application
  frontend:   # Vue.js SPA with Nginx
  db:         # PostgreSQL database
```

### Environment Management

#### Development Environment
- **Nix Flakes**: Reproducible development setup
- **Hot Reload**: Frontend and backend live reloading
- **Local Database**: H2 for development testing

#### Testing Environment
- **Isolated Tests**: In-memory H2 database
- **Profile-Based Config**: Test-specific configurations
- **Mock Services**: Unit test isolation

#### Production Environment
- **Containerized**: Docker images for all services
- **Orchestrated**: Docker Compose for service coordination
- **External Database**: PostgreSQL with persistent volumes

## Quality Assurance

### Testing Strategy

#### Backend Testing Pyramid
```
End-to-End Tests (API Integration)
    ↓
Integration Tests (Spring Context)
    ↓
Unit Tests (Isolated Classes)
```

#### Test Coverage Areas
- **Unit Tests**: Business logic and utility functions
- **Integration Tests**: Controller and service interactions
- **API Tests**: HTTP endpoint verification
- **Component Tests**: Vue component behavior

### Code Quality Standards

#### Java Code Quality
- **Lombok**: Boilerplate reduction with annotations
- **Bean Validation**: Input validation with JSR-303
- **Exception Handling**: Proper error responses
- **Logging**: Structured logging with SLF4J

#### TypeScript Code Quality
- **Strict Mode**: Compile-time type checking
- **ESLint**: Code linting and formatting
- **Vue 3 Best Practices**: Composition API patterns
- **Error Boundaries**: Proper error handling

## Performance Considerations

### Backend Performance
- **Connection Pooling**: HikariCP for database connections
- **Caching**: Hibernate second-level caching
- **Lazy Loading**: Optimized entity relationships
- **Query Optimization**: Indexed database queries

### Frontend Performance
- **Code Splitting**: Vite-based chunk optimization
- **Tree Shaking**: Unused code elimination
- **Asset Optimization**: Compressed bundles and images
- **Lazy Loading**: Component-based code splitting

## Scalability Considerations

### Horizontal Scaling
- **Stateless Design**: JWT-based authentication
- **Database Sharding**: PostgreSQL partitioning support
- **Load Balancing**: Nginx reverse proxy configuration
- **CDN Integration**: Static asset delivery

### Vertical Scaling
- **Memory Management**: JVM tuning for Spring Boot
- **Database Optimization**: Query optimization and indexing
- **Caching Layers**: Redis integration points
- **Monitoring**: Application metrics and health checks

## Security Architecture

### Authentication & Authorization
- **JWT Tokens**: Stateless authentication
- **Password Security**: BCrypt hashing
- **Role-Based Access**: USER/ADMIN permissions
- **Secure Headers**: CORS and security configurations

### Data Protection
- **Input Validation**: Bean Validation on all inputs
- **SQL Injection Prevention**: JPA/Hibernate safeguards
- **XSS Protection**: Vue.js automatic escaping
- **CSRF Protection**: Stateless API design

## Deployment Architecture

### CI/CD Pipeline (Planned)
```
Source Code → Build → Test → Docker Build → Deploy
```

### Infrastructure as Code
- **Docker Compose**: Local development environment
- **Container Registry**: Docker image management
- **Environment Variables**: Configuration management
- **Health Checks**: Application monitoring

## Monitoring & Observability

### Application Metrics
- **Spring Boot Actuator**: Health and metrics endpoints
- **Database Monitoring**: Connection pool statistics
- **Performance Metrics**: Response times and throughput
- **Error Tracking**: Exception logging and alerting

### Logging Strategy
- **Structured Logging**: JSON format with context
- **Log Levels**: Environment-appropriate verbosity
- **Centralized Logging**: Aggregation and analysis
- **Security Logging**: Authentication and authorization events

## Future Enhancements

### Microservices Migration
- **Service Decomposition**: Separate authentication service
- **API Gateway**: Request routing and authentication
- **Service Discovery**: Eureka or Kubernetes DNS
- **Distributed Tracing**: Request correlation across services

### Advanced Features
- **OAuth2 Integration**: Social login support
- **Multi-Factor Authentication**: Enhanced security
- **Audit Logging**: Comprehensive user action tracking
- **Real-time Features**: WebSocket integration

### Cloud-Native Evolution
- **Kubernetes**: Container orchestration
- **Service Mesh**: Istio for traffic management
- **Cloud Databases**: Managed PostgreSQL services
- **CDN Integration**: Global asset delivery

---

This architecture documentation provides a comprehensive overview of the MindForge application's design principles, implementation patterns, and scalability considerations. The application demonstrates enterprise-grade development practices suitable for production deployment and team collaboration.
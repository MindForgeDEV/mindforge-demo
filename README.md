# MindForge Demo

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.5.22-4FC08D)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9.3-blue)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)](https://www.docker.com/)

A modern, production-ready full-stack web application demonstrating enterprise-grade development practices, clean architecture, and DevOps principles. Built with Java 21, Spring Boot 3, Vue 3, and TypeScript.

## 🚀 Features

### Backend (Java/Spring Boot)
- **JWT Authentication** with secure token management
- **RESTful API** designed with OpenAPI 3.1 specification
- **Clean Architecture** with clear separation of concerns
- **Database Integration** with PostgreSQL and JPA/Hibernate
- **Security** with Spring Security and OAuth2 Resource Server
- **Validation** with Bean Validation and custom constraints
- **Documentation** with OpenAPI/Swagger integration

### Frontend (Vue.js/TypeScript)
- **Modern Vue 3** with Composition API
- **Type-Safe Development** with strict TypeScript configuration
- **Component Architecture** with reusable Vue components
- **API Integration** with Axios and automatic JWT handling
- **Responsive Design** with scoped CSS styling
- **Build Optimization** with Vite bundler

### DevOps & Infrastructure
- **Containerization** with Docker and Docker Compose
- **Multi-Environment Support** (development, test, production)
- **Database Migrations** with Liquibase
- **Development Environment** with Nix Flakes
- **CI/CD Ready** with Gradle and npm scripts

## 🏗️ Architecture

### Backend Architecture
```
mindforge-be/
├── config/           # Configuration classes (Security, DataSource, Beans)
├── controller/       # REST controllers with OpenAPI annotations
├── dto/             # Data Transfer Objects with validation
├── model/           # JPA entities
├── repository/      # Data access layer
├── service/         # Business logic layer
└── MindforgeApplication.java
```

**Design Patterns Implemented:**
- **Dependency Injection** with Spring's `@Autowired` and Lombok's `@RequiredArgsConstructor`
- **Builder Pattern** for complex object construction
- **Repository Pattern** for data access abstraction
- **Service Layer Pattern** for business logic encapsulation
- **DTO Pattern** for API contract definition

### Frontend Architecture
```
mindforge-fe/
├── api/             # API client with type-safe endpoints
├── components/      # Reusable Vue components
├── App.vue          # Root component
├── main.ts          # Application entry point
└── style.css        # Global styles
```

**Modern Frontend Practices:**
- **Composition API** for reactive component logic
- **TypeScript Strict Mode** for compile-time type checking
- **API Layer Abstraction** with centralized HTTP client
- **Component Isolation** with scoped styling

## 🛠️ Technology Stack

### Backend
- **Java 21** - Latest LTS with modern language features
- **Spring Boot 3.5.7** - Production-ready framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **JWT (JJWT)** - Token-based authentication
- **PostgreSQL** - Robust relational database
- **H2 Database** - In-memory database for testing
- **Liquibase** - Database migration tool
- **OpenAPI Generator** - API client/server code generation
- **Lombok** - Boilerplate code reduction
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework for unit tests

### Frontend
- **Vue 3.5.22** - Progressive JavaScript framework
- **TypeScript 5.9.3** - Type-safe JavaScript superset
- **Vite** - Fast build tool and dev server
- **Axios** - HTTP client with interceptors
- **Vue TSC** - TypeScript compiler for Vue

### DevOps & Tools
- **Docker & Docker Compose** - Container orchestration
- **Nix Flakes** - Reproducible development environment
- **Gradle** - Build automation for Java
- **npm** - Package management for Node.js
- **Git** - Version control with conventional commits

## 📋 Prerequisites

- **Java 21** or higher
- **Node.js 18+** and npm
- **Docker & Docker Compose** (for full deployment)
- **Git** for version control

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/mindforge-demo.git
cd mindforge-demo
```

### 2. Environment Setup
Create a `.env` file in the root directory:
```env
# Database
POSTGRES_DB=mindforge
POSTGRES_USER=mindforge_user
POSTGRES_PASSWORD=secure_password_123
POSTGRES_PORT=5432

# Application Ports
BACKEND_PORT=8080
FRONTEND_PORT=3000

# JWT Configuration
JWT_SECRET=verylongrandomstringwithatleast32charactersforjwttesting!!!
JWT_EXPIRATION=3600000

# Frontend Configuration
VITE_API_URL=http://localhost:8080
```

### 3. Development Environment (Recommended)

#### Using Nix (Reproducible)
```bash
# If you have Nix installed
nix develop
```

#### Manual Setup
```bash
# Backend
cd mindforge-be
./gradlew build

# Frontend
cd ../mindforge-fe
npm install
```

### 4. Run with Docker Compose (Full Stack)
```bash
docker-compose up --build
```

Access the application:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html

### 5. Manual Development Setup

#### Backend Development
```bash
cd mindforge-be
./gradlew runDev
```

#### Frontend Development
```bash
cd mindforge-fe
npm run dev
```

## 🧪 Testing

### Backend Testing
```bash
cd mindforge-be

# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "*AuthServiceUnitTest*"

# Run with coverage (if configured)
./gradlew test jacocoTestReport
```

**Test Structure:**
- **Unit Tests**: Business logic testing with Mockito mocks
- **Integration Tests**: Full Spring context testing (currently has JWT configuration issues)
- **Test Frameworks**: JUnit 5, AssertJ, Mockito

### Frontend Testing
```bash
cd mindforge-fe

# Type checking
npm run build

# Run tests (when implemented)
npm test
```

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securepassword123"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securepassword123"
}
```

**Response:**
```json
{
  "username": "johndoe",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Get Current User
```http
GET /auth/me
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "id": 1,
  "username": "johndoe",
  "role": "USER"
}
```

### OpenAPI Specification
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Specification File**: `mindforge-be/src/main/openapi/openapi.yaml`

## 🔧 Development Workflow

### Code Quality
- **Java**: Lombok for boilerplate reduction, Bean Validation for input validation
- **TypeScript**: Strict mode with unused variable detection
- **Architecture**: Clean separation with controller → service → repository layers

### Best Practices Demonstrated
- **OpenAPI-First Development**: API contracts defined before implementation
- **Type Safety**: Full TypeScript coverage on frontend, strong typing on backend
- **Security**: JWT authentication, password encoding, CORS configuration
- **Testing**: Unit tests with mocking, integration test structure
- **Containerization**: Docker for consistent deployment
- **Environment Management**: Profile-based configuration

### Git Workflow
```bash
# Feature development
git checkout -b feature/user-authentication
# Make changes...
git commit -m "feat: implement JWT authentication"

# Testing
./gradlew test
npm run build

# Code review and merge
git push origin feature/user-authentication
```

## 🚢 Deployment

### Production Build
```bash
# Backend
cd mindforge-be
./gradlew bootJar

# Frontend
cd ../mindforge-fe
npm run build
```

### Docker Deployment
```bash
# Build and run production containers
docker-compose -f docker-compose.yml up --build -d
```

### Environment Variables for Production
```env
SPRING_PROFILES_ACTIVE=prod
POSTGRES_HOST=production-db-host
POSTGRES_PASSWORD=secure-production-password
JWT_SECRET=production-specific-jwt-secret-32-chars-minimum
```

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Commit** changes: `git commit -m 'feat: add amazing feature'`
4. **Push** to branch: `git push origin feature/amazing-feature`
5. **Open** a Pull Request

### Code Style Guidelines

#### Java (Backend)
- Use Lombok annotations for boilerplate code
- Follow Spring Boot naming conventions
- Use Optional for nullable returns
- Implement proper exception handling

#### TypeScript/Vue (Frontend)
- Use Composition API with `<script setup>`
- Enable strict TypeScript mode
- Use `ref()` for reactive state
- Implement proper error boundaries

## 📈 Project Status

### ✅ Completed Features
- JWT-based authentication system
- User registration and login
- RESTful API with OpenAPI documentation
- Type-safe frontend with Vue 3 + TypeScript
- Docker containerization
- Database integration with PostgreSQL
- Clean architecture implementation
- OpenAPI-first development approach

### 🚧 In Progress / Known Issues
- Integration tests failing due to JWT configuration conflicts
- Frontend testing framework not yet implemented
- End-to-end testing pipeline missing
- CI/CD pipeline not configured

### 🎯 Future Enhancements
- User profile management
- Role-based access control (RBAC)
- Email verification for registration
- Password reset functionality
- API rate limiting
- Comprehensive test coverage
- Monitoring and logging
- API versioning strategy

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Your Name**
- LinkedIn: [Your LinkedIn Profile](https://linkedin.com/in/yourprofile)
- GitHub: [Your GitHub Profile](https://github.com/yourusername)
- Email: your.email@example.com

---

**Note**: This project demonstrates modern full-stack development practices and is intended as a portfolio piece for job applications. It showcases expertise in Java/Spring Boot, Vue.js/TypeScript, and DevOps practices.

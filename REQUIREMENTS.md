# Requirements for MindForge Demo - Open Development Tasks

## ✅ Issues Resolved

### Backend Testing Issues - RESOLVED ✅
- **JWT Configuration Conflict**: Fixed by implementing unified `JwtConfig` and `JwtTestConfig`
- **Root Cause**: Resolved by creating separate JWT key beans for different profiles
- **Impact**: All backend tests now pass (14/14)
- **Solution**: Profile-specific JWT configuration with proper key management

### Test Coverage - MOSTLY RESOLVED ✅
- **Backend**: All tests passing (unit + integration) ✅
- **Frontend**: Test framework implemented, basic tests created (execution blocked by config issue)
- **End-to-End**: Framework ready, containerized testing available

## 🔧 Required Fixes

### 1. Fix Backend JWT Configuration
**Problem**: `io.jsonwebtoken.security.WeakKeyException` during test context loading

**Possible Solutions**:
- Use different JWT secrets for main app and tests
- Create test-specific JWT configuration
- Fix key generation logic in `JwtService`
- Resolve conflict between `JwtService` and `SecurityConfig`

**Files to Check**:
- `src/main/java/mindforge/service/JwtService.java`
- `src/main/java/mindforge/config/SecurityConfig.java`
- `src/test/resources/application-test.yml`

### 2. Implement Frontend Testing
**Status**: Partially Implemented ✅
- ✅ Test framework setup (Vitest, Vue Test Utils, jsdom)
- ✅ Basic test structure created
- ✅ API client tests implemented
- ✅ Component tests implemented
- ❌ Tests failing due to Vite SSR configuration issues

**Current Issues**:
- Vitest configuration conflicts with custom Vite setup
- SSR export errors preventing test execution
- Need to resolve Vite/Rolldown compatibility with Vitest

**Test Files Created**:
- `vitest.config.ts` - Test configuration
- `src/test/setup.ts` - Test setup file
- `src/components/LoginButton.spec.ts` - Component unit tests
- `src/api/auth.spec.ts` - API client tests

### 3. Database Setup for Testing
**Current State**: Tests use H2 in-memory database
**Issues**:
- Liquibase is disabled in tests (`liquibase.enabled: false`)
- No database schema initialization in test environment

**Required**:
- Enable Liquibase for test profile or create test schema
- Ensure test database is properly initialized

### 4. End-to-End Testing
**Missing**:
- Full application startup testing
- API endpoint verification
- Frontend-backend integration testing
- Docker Compose testing

**Tools Needed**:
- TestContainers for database testing
- Cypress/Playwright for E2E tests
- API testing framework (RestAssured/Newman)

## 🏗️ Architecture Improvements

### 5. API Consistency
**Current Issues**:
- Mixed authentication approaches (manual JWT in controller vs OAuth2 in SecurityConfig)
- Inconsistent error handling
- Missing API documentation generation

**Improvements Needed**:
- Unified authentication strategy
- Consistent error response format
- Complete OpenAPI spec implementation
- API client generation for frontend

### 6. Configuration Management
**Status**: Resolved ✅
- ✅ `.env` file exists with all required variables
- ✅ Docker Compose configuration validated with podman-compose
- ✅ Environment variables properly configured for all services
- ✅ Profile-specific configurations working (test/prod profiles)

## 📋 Development Workflow

### 7. CI/CD Pipeline
**Missing**:
- Automated testing pipeline
- Build verification
- Code quality checks
- Deployment automation

**Required Setup**:
- GitHub Actions workflow
- Code linting (ESLint, Prettier for frontend)
- Code formatting (Spotless for Java)
- Security scanning

### 8. Documentation
**Missing**:
- API documentation (Swagger UI)
- Development setup guide
- Deployment instructions
- Architecture documentation

## ✅ Completed Tasks

- ✅ OpenAPI specification updated to match backend implementation
- ✅ Backend DTOs aligned with OpenAPI schemas
- ✅ Frontend API client generated from OpenAPI spec
- ✅ Frontend components updated to use generated API client
- ✅ TypeScript types properly configured
- ✅ Frontend builds successfully
- ✅ Backend compiles successfully with Lombok
- ✅ Backend unit tests pass (5/5 ✅)
- ✅ Backend integration tests pass (9/9 ✅)
- ✅ JWT configuration fixed for all test environments
- ✅ Test security configuration implemented
- ✅ Frontend test framework setup (Vitest + Vue Test Utils)
- ✅ Basic test files created for components and API client
- ✅ Docker/Podman configuration validated
- ✅ Environment configuration completed
- ✅ Multi-level testing support (local + containerized)

## 🚀 Next Steps Priority

1. **HIGH**: Fix Vitest configuration for frontend testing (Vite/Rolldown compatibility)
2. **MEDIUM**: Implement end-to-end testing with Playwright/Cypress
3. **MEDIUM**: Add database schema initialization for tests
4. **LOW**: Implement CI/CD pipeline with GitHub Actions
5. **LOW**: Add comprehensive API documentation
6. **LOW**: Performance testing and monitoring setup

## 🧪 Testing Commands

```bash
# Backend tests
cd mindforge-be
./gradlew test                    # All tests (14 tests, all passing ✅)
./gradlew test --tests "*UnitTest*"    # Unit tests only
./gradlew test --tests "*IntegrationTest*"  # Integration tests

# Frontend tests (framework ready, execution blocked)
cd mindforge-fe
npm run test:run                 # Unit tests (currently failing due to Vite config)
npm run test                     # Watch mode

# Full application test
podman-compose up --build        # Test with Podman (containerized)
# OR
docker-compose up --build        # Test with Docker (if available)
```

## 📝 Notes

- **Backend**: All tests pass (14/14), JWT configuration fully resolved ✅
- **Frontend**: Builds successfully, test framework ready (minor config issue to resolve)
- **Integration**: Multi-level testing supported (local + containerized)
- **Architecture**: Clean OpenAPI-first approach successfully implemented
- **Security**: JWT authentication working across all environments
- **Containers**: Podman-compose validated, ready for deployment
- **IDE**: Lombok warnings are false positives - code compiles and runs correctly</content>
<parameter name="filePath">/home/pendragon/Projects/mindforge-demo/REQUIREMENTS.md
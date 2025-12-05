# Environment Configuration Guide

This document explains how to work with the MindForge project's environment configuration system.

## Overview

The project uses a comprehensive environment variable system that supports multiple deployment profiles (development, production, testing) with secure secret management.

## Environment Files

### `.env.example` (Template)
- **Purpose**: Documentation and template for all required environment variables
- **Usage**: Copy to create new environment files
- **Committed**: Yes (safe, contains no secrets)

### `.env` (Development)
- **Purpose**: Development environment configuration
- **Usage**: Automatically loaded by Docker Compose and dev scripts
- **Committed**: No (contains development secrets)

### `.env.prod` (Production)
- **Purpose**: Production environment configuration
- **Usage**: Load with `--env-file .env.prod` in production
- **Committed**: No (contains production secrets)

### `.env.test` (Testing)
- **Purpose**: Test environment configuration
- **Usage**: Used by test suites and CI/CD
- **Committed**: No (contains test secrets)

## Quick Start

```bash
# 1. Copy the template
cp .env.example .env

# 2. Edit with your values (optional for development)
nano .env

# 3. Start development environment
./dev.sh
```

## Environment Variables Reference

### Database Configuration
```env
POSTGRES_USER=mindforge_user          # Database username
POSTGRES_PASSWORD=your_password       # Database password
POSTGRES_DB=mindforge                 # Database name
POSTGRES_PORT=5432                    # Database port
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mindforge  # Full JDBC URL
```

### Application Ports
```env
BACKEND_PORT=8080     # Backend API port (Docker external port)
FRONTEND_PORT=80      # Frontend port (Docker external port)
SERVER_PORT=8081      # Backend port (local development, internal container port is 8080)
```

### Security Configuration
```env
JWT_SECRET=your_32+_char_secret       # JWT signing key
JWT_EXPIRATION=3600000                # Token expiration (ms)
```

### URLs and Endpoints
```env
FRONTEND_URL=http://localhost:3000    # Frontend URL for CORS
VITE_API_URL=http://localhost:8081    # API URL for frontend
```

### Profile Selection
```env
SPRING_PROFILES_ACTIVE=dev  # dev | prod | test
```

## Profile-Specific Behavior

### Development Profile (`dev`)
- **Database**: PostgreSQL with fallback to H2
- **Logging**: DEBUG level
- **Security**: Disabled headers, no rate limiting
- **JWT**: 1 hour expiration
- **CORS**: Permissive

### Production Profile (`prod`)
- **Database**: PostgreSQL required
- **Logging**: INFO level
- **Security**: Enabled headers, rate limiting
- **JWT**: 24 hour expiration
- **CORS**: Restricted to FRONTEND_URL

### Test Profile (`test`)
- **Database**: H2 in-memory (fast)
- **Logging**: DEBUG level
- **Security**: Minimal
- **JWT**: 5 minute expiration, test-specific secret
- **CORS**: Permissive

### Docker Profile (`docker`)
- **Database**: PostgreSQL required
- **Logging**: DEBUG level (configurable via LOG_LEVEL)
- **Security**: Configurable via environment variables
- **JWT**: Configurable expiration
- **CORS**: Configurable
- **Liquibase**: Enabled by default
- **Hibernate DDL**: Validate mode (safe for production)

## Usage Examples

### Development
```bash
# Using dev.sh (recommended)
./dev.sh

# Manual backend start
cd mindforge-be
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

# Manual frontend start
cd mindforge-fe
npm run dev
```

### Production
```bash
# Using Docker Compose
docker-compose --env-file .env.prod up -d

# Manual deployment
export SPRING_PROFILES_ACTIVE=prod
export $(grep -v '^#' .env.prod | xargs)
./gradlew bootRun
```

### Testing
```bash
# Unit tests
cd mindforge-be
./gradlew test

# Frontend tests
cd mindforge-fe
npm run test:run

# Environment validation
./test-env-config.sh
```

## Recent Configuration Improvements

### Code Quality Enhancements
- **Environment Variables**: Removed hardcoded values from docker-compose.yml and application configs
- **Lombok Optimization**: DTOs now use proper Lombok annotations instead of manual methods
- **Security Hardening**: JWT secrets properly externalized, test-specific secrets implemented
- **Database Safety**: Hibernate DDL changed from 'create' to 'validate' for Docker deployments

### Configuration Best Practices
- **Separation of Concerns**: Environment-specific configs separated from main application.yml
- **Flexible Liquibase**: Configurable Liquibase activation via LIQUIBASE_ENABLED variable
- **Clean Docker Config**: No hardcoded credentials in compose files

## Security Best Practices

1. **Never commit secrets**: `.env*` files are in `.gitignore`
2. **Use strong secrets**: JWT secrets should be 32+ characters
3. **Environment-specific secrets**: Different secrets for dev/prod/test
4. **Principle of least privilege**: Minimal required permissions
5. **Regular rotation**: Rotate secrets periodically
6. **Configuration Security**: No hardcoded secrets in application code

## Troubleshooting

### Environment Variables Not Loading
```bash
# Check if .env file exists
ls -la .env

# Validate syntax
bash test-env-config.sh

# Check Spring Boot logs for variable resolution
cd mindforge-be
./gradlew bootRun --debug
```

### Database Connection Issues
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Test connection
psql -h localhost -p 5432 -U mindforge_user -d mindforge

# Check environment variables
echo $POSTGRES_USER $POSTGRES_PASSWORD
```

### Profile Not Activating
```bash
# Check profile setting
echo $SPRING_PROFILES_ACTIVE

# Check in application logs
grep "profile" mindforge-be/build/logs/*.log
```

## Advanced Configuration

### Custom Environment Files
```bash
# Create custom environment
cp .env.example .env.staging
nano .env.staging

# Use with Docker Compose
docker-compose --env-file .env.staging up
```

### Environment Variable Precedence
1. System environment variables
2. `--env-file` specified files
3. `.env` (automatic)
4. Spring Boot `application.yml` defaults

### CI/CD Integration
```yaml
# GitHub Actions example
- name: Run tests
  env:
    SPRING_PROFILES_ACTIVE: test
  run: ./gradlew test
```

## Support

For issues with environment configuration:
1. Run `./test-env-config.sh` for diagnostics
2. Check logs in `mindforge-be/build/logs/`
3. Verify `.env` file syntax
4. Ensure required services are running

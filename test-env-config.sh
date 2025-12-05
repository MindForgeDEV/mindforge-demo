#!/bin/bash

# Test Environment Configuration Script
# Tests that environment configurations correctly apply according to the chosen profile

set -e

echo "🧪 Testing Environment Configurations..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print status
print_status() {
    local status=$1
    local message=$2
    if [ "$status" = "success" ]; then
        echo -e "${GREEN}✅ $message${NC}"
    elif [ "$status" = "warning" ]; then
        echo -e "${YELLOW}⚠️  $message${NC}"
    else
        echo -e "${RED}❌ $message${NC}"
    fi
}

# Function to test environment file loading
test_env_file() {
    local env_file=$1
    local profile=$2
    
    echo -e "\n${BLUE}Testing $env_file for $profile profile:${NC}"
    
    if [ ! -f "$env_file" ]; then
        print_status "error" "$env_file not found"
        return 1
    fi
    
    # Load environment variables from file
    export $(grep -v '^#' "$env_file" | xargs 2>/dev/null || true)
    
    # Test key variables are set
    local required_vars=("SPRING_PROFILES_ACTIVE" "JWT_SECRET" "POSTGRES_USER")
    local all_set=true
    
    for var in "${required_vars[@]}"; do
        if [ -z "${!var}" ]; then
            print_status "error" "$var not set in $env_file"
            all_set=false
        fi
    done
    
    if [ "$all_set" = true ]; then
        print_status "success" "All required variables set in $env_file"
    fi
    
    # Test profile-specific settings
    case $profile in
        "dev")
            if [ "${SPRING_PROFILES_ACTIVE}" = "dev" ]; then
                print_status "success" "Development profile correctly set"
            else
                print_status "error" "Expected dev profile, got ${SPRING_PROFILES_ACTIVE}"
            fi
            ;;
        "prod")
            if [ "${SPRING_PROFILES_ACTIVE}" = "prod" ]; then
                print_status "success" "Production profile correctly set"
            else
                print_status "error" "Expected prod profile, got ${SPRING_PROFILES_ACTIVE}"
            fi
            ;;
        "test")
            if [ "${SPRING_PROFILES_ACTIVE}" = "test" ]; then
                print_status "success" "Test profile correctly set"
            else
                print_status "error" "Expected test profile, got ${SPRING_PROFILES_ACTIVE}"
            fi
            ;;
    esac
    
    return 0
}

# Function to test Spring Boot configuration files
test_spring_config() {
    local profile=$1
    local expected_port=$2

    echo -e "\n${BLUE}Testing Spring Boot configuration for $profile profile:${NC}"

    # Check if the config file exists
    local config_file="mindforge-be/src/main/resources/application-$profile.yml"
    if [ "$profile" = "test" ]; then
        config_file="mindforge-be/src/test/resources/application-test.yml"
    fi

    if [ ! -f "$config_file" ]; then
        print_status "error" "Configuration file $config_file not found"
        return 1
    fi

    # Check if the config file contains expected environment variable references
    if grep -q "\${.*}" "$config_file"; then
        print_status "success" "$profile configuration uses environment variables"
    else
        print_status "warning" "$profile configuration may not use environment variables"
    fi

    # Test that gradlew can resolve dependencies (quick check)
    cd mindforge-be
    if timeout 30 ./gradlew dependencies --quiet > /dev/null 2>&1; then
        print_status "success" "Gradle build configuration is valid for $profile profile"
    else
        print_status "error" "Gradle configuration has issues"
    fi
    cd ..
}

# Function to test Docker Compose configuration
test_docker_compose() {
    echo -e "\n${BLUE}Testing Docker Compose configuration:${NC}"

    if [ ! -f "docker-compose.yml" ]; then
        print_status "error" "docker-compose.yml not found"
        return 1
    fi

    # Basic syntax check - look for common YAML structure
    if grep -q "^services:" docker-compose.yml; then
        print_status "success" "Services section found in docker-compose.yml"
    else
        print_status "error" "No services section in docker-compose.yml"
        return 1
    fi

    # Check for required services
    if grep -q "^  db:" docker-compose.yml; then
        print_status "success" "Database service defined"
    else
        print_status "error" "Database service not found"
    fi

    if grep -q "^  backend:" docker-compose.yml; then
        print_status "success" "Backend service defined"
    else
        print_status "error" "Backend service not found"
    fi

    if grep -q "^  frontend:" docker-compose.yml; then
        print_status "success" "Frontend service defined"
    else
        print_status "error" "Frontend service not found"
    fi

    # Check for environment variable usage
    if grep -q "\${.*}" docker-compose.yml; then
        print_status "success" "Docker Compose uses environment variables"
    else
        print_status "warning" "Docker Compose may not use environment variables"
    fi

    print_status "success" "Docker Compose configuration structure is valid"
}

# Main test execution
echo "🚀 Starting Environment Configuration Tests..."

# Test environment files
test_env_file ".env" "dev"
test_env_file ".env.prod" "prod" 
test_env_file ".env.test" "test"

# Test Spring Boot configurations
test_spring_config "dev" "8081"
test_spring_config "prod" "8080"
test_spring_config "test" "8082"

# Test Docker Compose
test_docker_compose

# Test .env.example exists and is properly documented
echo -e "\n${BLUE}Testing .env.example template:${NC}"
if [ -f ".env.example" ]; then
    print_status "success" ".env.example template exists"
    
    # Check if it has required sections
    if grep -q "DATABASE CONFIGURATION" .env.example; then
        print_status "success" ".env.example has database configuration section"
    fi
    
    if grep -q "JWT CONFIGURATION" .env.example; then
        print_status "success" ".env.example has JWT configuration section"
    fi
else
    print_status "error" ".env.example template not found"
fi

# Test that .env files are in .gitignore
echo -e "\n${BLUE}Testing .gitignore configuration:${NC}"
if grep -q "^\.env$" .gitignore; then
    print_status "success" ".env is in .gitignore"
else
    print_status "error" ".env not found in .gitignore"
fi

if grep -q "^\.env\.prod$" .gitignore; then
    print_status "success" ".env.prod is in .gitignore"
else
    print_status "error" ".env.prod not found in .gitignore"
fi

if grep -q "^\.env\.test$" .gitignore; then
    print_status "success" ".env.test is in .gitignore"
else
    print_status "error" ".env.test not found in .gitignore"
fi

echo -e "\n${GREEN}🎉 Environment configuration testing completed!${NC}"
echo "Review the results above and fix any issues found."

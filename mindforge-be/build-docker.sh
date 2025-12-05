#!/bin/bash

# MindForge Backend Docker Build Script
# Builds JAR locally and then creates Docker image

set -e

echo "🚀 Building MindForge Backend Docker Image..."

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print status
print_status() {
    local status=$1
    local message=$2
    if [ "$status" = "success" ]; then
        echo -e "${GREEN}✅ $message${NC}"
    elif [ "$status" = "info" ]; then
        echo -e "${BLUE}ℹ️  $message${NC}"
    else
        echo -e "${RED}❌ $message${NC}"
    fi
}

# Check if JAR exists and is up to date
check_jar() {
    if [ -f "build/libs/demo-0.0.1-SNAPSHOT.jar" ]; then
        jar_mtime=$(stat -c %Y build/libs/demo-0.0.1-SNAPSHOT.jar 2>/dev/null || stat -f %m build/libs/demo-0.0.1-SNAPSHOT.jar)
        src_mtime=$(find src -name "*.java" -o -name "*.kt" | head -1 | xargs stat -c %Y 2>/dev/null || find src -name "*.java" -o -name "*.kt" | head -1 | xargs stat -f %m)
        
        if [ "$jar_mtime" -gt "$src_mtime" ] 2>/dev/null; then
            print_status "info" "JAR is up to date"
            return 0
        fi
    fi
    return 1
}

# Build JAR locally
build_jar() {
    print_status "info" "Building JAR locally (this may take a moment)..."
    
    if ./gradlew bootJar -x test --no-daemon --quiet; then
        print_status "success" "JAR built successfully"
    else
        print_status "error" "Failed to build JAR"
        exit 1
    fi
}

# Build Docker image
build_docker() {
    print_status "info" "Building Docker image..."
    
    if docker build -t mindforge-be:latest . --quiet; then
        print_status "success" "Docker image built successfully"
        echo -e "${BLUE}📋 Image: mindforge-be:latest${NC}"
    else
        print_status "error" "Failed to build Docker image"
        exit 1
    fi
}

# Main build process
main() {
    echo -e "${BLUE}🔧 MindForge Backend Docker Build${NC}"
    echo "=================================="
    
    # Check if JAR needs rebuilding
    if ! check_jar; then
        build_jar
    else
        print_status "info" "Skipping JAR build (already up to date)"
    fi
    
    # Build Docker image
    build_docker
    
    echo ""
    print_status "success" "Build completed successfully!"
    echo -e "${BLUE}🚀 Run with: docker run -p 8080:8080 mindforge-be:latest${NC}"
}

# Run main function
main "$@"

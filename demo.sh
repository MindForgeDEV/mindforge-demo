#!/bin/bash
# MindForge Demo Setup Script
# This script sets up the complete MindForge demo environment

set -e

echo "🚀 MindForge Demo Setup"
echo "========================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Podman is available
if ! command -v podman &> /dev/null; then
    print_error "Podman is not installed. Please install Podman first."
    exit 1
fi

# Check if npm is available
if ! command -v npm &> /dev/null; then
    print_error "npm is not installed. Please install Node.js first."
    exit 1
fi

# Setup function
setup() {
    print_status "Setting up MindForge demo environment..."

    # Start PostgreSQL database
    print_status "Starting PostgreSQL database..."
    podman-compose -f dev-compose.yml up -d db

    # Wait for database to be ready
    print_status "Waiting for database to be ready..."
    sleep 10

    # Start backend
    print_status "Starting backend server..."
    cd mindforge-be
    nohup bash dev.sh > dev.log 2>&1 &
    BACKEND_PID=$!
    echo $BACKEND_PID > ../backend.pid
    cd ..

    # Wait for backend to start
    print_status "Waiting for backend to start..."
    sleep 15

    # Start frontend
    print_status "Starting frontend server..."
    cd mindforge-fe
    nohup npm run dev > fe.log 2>&1 &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > ../frontend.pid
    cd ..

    print_status "MindForge demo is now running!"
    print_status "Backend: http://localhost:8080"
    print_status "Frontend: http://localhost:3000"
    print_status "API Docs: http://localhost:8080/swagger-ui.html"
    print_status ""
    print_status "Demo users:"
    print_status "  Admin: admin / mindforge123"
    print_status "  User: testuser1 / mindforge123"
    print_status "  User: testuser2 / mindforge123"
}

# Teardown function
teardown() {
    print_status "Tearing down MindForge demo environment..."

    # Stop frontend
    if [ -f frontend.pid ]; then
        FRONTEND_PID=$(cat frontend.pid)
        if kill -0 $FRONTEND_PID 2>/dev/null; then
            print_status "Stopping frontend..."
            kill $FRONTEND_PID
        fi
        rm frontend.pid
    fi

    # Stop backend
    if [ -f backend.pid ]; then
        BACKEND_PID=$(cat backend.pid)
        if kill -0 $BACKEND_PID 2>/dev/null; then
            print_status "Stopping backend..."
            kill $BACKEND_PID
        fi
        rm backend.pid
    fi

    # Stop database
    print_status "Stopping database..."
    podman-compose -f dev-compose.yml down

    print_status "MindForge demo environment cleaned up!"
}

# Status function
status() {
    echo "MindForge Demo Status"
    echo "====================="

    # Check database
    if podman-compose -f dev-compose.yml ps | grep -q "Up"; then
        echo -e "${GREEN}✓${NC} Database: Running"
    else
        echo -e "${RED}✗${NC} Database: Stopped"
    fi

    # Check backend
    if [ -f backend.pid ] && kill -0 $(cat backend.pid) 2>/dev/null; then
        echo -e "${GREEN}✓${NC} Backend: Running (PID: $(cat backend.pid))"
    else
        echo -e "${RED}✗${NC} Backend: Stopped"
    fi

    # Check frontend
    if [ -f frontend.pid ] && kill -0 $(cat frontend.pid) 2>/dev/null; then
        echo -e "${GREEN}✓${NC} Frontend: Running (PID: $(cat frontend.pid))"
    else
        echo -e "${RED}✗${NC} Frontend: Stopped"
    fi

    echo ""
    echo "URLs:"
    echo "  Backend: http://localhost:8080"
    echo "  Frontend: http://localhost:3000"
    echo "  API Docs: http://localhost:8080/swagger-ui.html"
}

# Main script logic
case "${1:-setup}" in
    setup)
        setup
        ;;
    teardown)
        teardown
        ;;
    restart)
        teardown
        setup
        ;;
    status)
        status
        ;;
    *)
        echo "Usage: $0 {setup|teardown|restart|status}"
        echo ""
        echo "Commands:"
        echo "  setup    - Start the complete demo environment"
        echo "  teardown - Stop and clean up the demo environment"
        echo "  restart  - Restart the demo environment"
        echo "  status   - Show current status of services"
        exit 1
        ;;
esac
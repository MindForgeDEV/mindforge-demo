#!/bin/bash

# MindForge Development Mode Script
# Starts PostgreSQL in container, backend with hot reload, frontend with hot reload

set -e

echo "🚀 Starting MindForge in Development Mode..."

# Start PostgreSQL
echo "📦 Starting PostgreSQL..."
docker compose up -d db

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 15

# Start backend with hot reload
echo "🔧 Starting backend (Spring Boot with devtools)..."
cd mindforge-be
./gradlew bootRun -Dspring.profiles.active=dev &
BACKEND_PID=$!
cd ..

# Start frontend with hot reload
echo "🎨 Starting frontend (Vue with Vite)..."
cd mindforge-fe
npm run dev &
FRONTEND_PID=$!
cd ..

echo "✅ All services started!"
echo "🌐 Frontend: http://localhost:3000"
echo "🔗 Backend API: http://localhost:8080"
echo "🗄️  Database: localhost:5432"
echo ""
echo "Press Ctrl+C to stop all services"

# Wait for interrupt
trap "echo '🛑 Stopping services...'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; docker compose down" INT TERM

wait
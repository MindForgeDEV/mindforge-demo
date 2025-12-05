#!/bin/bash
# Development script for Mindforge Backend
# Runs the application with dev profile

cd "$(dirname "$0")"
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mindforge
export POSTGRES_USER=mindforge
export POSTGRES_PASSWORD=mindforge123
./gradlew runDev
# Agent Instructions for Mindforge Demo

## Build/Lint/Test Commands
- **Backend build**: `cd mindforge-be && ./gradlew build`
- **Backend test all**: `cd mindforge-be && ./gradlew test`
- **Backend test single**: `cd mindforge-be && ./gradlew test --tests "*TestName*"`
- **Frontend build**: `cd mindforge-fe && npm run build`
- **Frontend test all**: `cd mindforge-fe && npm run test:run`
- **Frontend type check**: `cd mindforge-fe && npx vue-tsc --noEmit`

## Code Style Guidelines
- **Java**: Lombok annotations (@Service, @RequiredArgsConstructor), camelCase methods/variables, PascalCase classes, Optional for nullables, try-catch in controllers, @Valid validation, @Builder for DTOs
- **TypeScript/Vue**: `<script setup lang="ts">`, ref() for reactivity, ES6 imports (external first), camelCase vars/functions, PascalCase components, try-catch with axios, strict TS, scoped styles
- **General**: 4-space Java indent, 2-space TS indent, minimal comments, clean architecture (controllers/services/repos), conventional commits</content>
<parameter name="filePath">/home/pendragon/Projects/mindforge-demo/AGENTS.md
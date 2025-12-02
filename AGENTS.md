# Agent Instructions for Mindforge Demo

## Build/Lint/Test Commands

### Backend (Java/Spring Boot)
- **Build**: `./gradlew build` or `./gradlew bootJar`
- **Test all**: `./gradlew test`
- **Test single**: `./gradlew test --tests "*TestName*"` (e.g., `./gradlew test --tests "*AuthServiceUnitTest*"`)
- **Run dev**: `./gradlew runDev`
- **Run prod**: `./gradlew runProd`

### Frontend (Vue/TypeScript)
- **Build**: `cd mindforge-fe && npm run build`
- **Dev server**: `cd mindforge-fe && npm run dev`
- **Type check**: `cd mindforge-fe && npx vue-tsc --noEmit`

## Code Style Guidelines

### Java (Backend)
- **Imports**: Standard Java imports first, then Spring framework, then project imports (separated by blank lines)
- **Annotations**: Use Lombok (@RequiredArgsConstructor, @Service, etc.)
- **Naming**: camelCase for methods/variables, PascalCase for classes/interfaces
- **Null safety**: Use Optional for nullable returns, avoid null checks where possible
- **Error handling**: Controllers use try-catch with ResponseEntity, services throw exceptions
- **Builders**: Use builder pattern for DTOs and complex objects
- **Validation**: Use @Valid and Bean Validation annotations

### TypeScript/Vue (Frontend)
- **Script setup**: Use `<script setup lang="ts">` for Composition API
- **Reactivity**: Use `ref()` for reactive variables, avoid direct mutations
- **Imports**: ES6 imports, group by external libraries then internal
- **Naming**: camelCase for variables/functions, PascalCase for components
- **Error handling**: Try-catch with axios, handle HTTP status codes appropriately
- **Types**: Strict TypeScript with noUnusedLocals/noUnusedParameters enabled
- **Styling**: Scoped styles in Vue components, CSS-in-JS approach

### General
- **Formatting**: Follow language-specific conventions (Java: 4-space indent, TS: 2-space)
- **Comments**: Minimal comments, focus on self-documenting code
- **Architecture**: Clean separation between controllers, services, repositories</content>
<parameter name="filePath">/home/pendragon/Projects/mindforge-demo/AGENTS.md
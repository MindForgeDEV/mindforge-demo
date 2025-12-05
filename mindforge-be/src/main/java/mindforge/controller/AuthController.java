package mindforge.controller;

import lombok.RequiredArgsConstructor;
import mindforge.dto.AuthenticationResponseDto;
import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.service.AuthenticationService;
import mindforge.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User successfully created"),
        @ApiResponse(responseCode = "409", description = "Username already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto request) {
        try {
            if (authService.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.status(409)
                        .body(Map.of("error", "Benutzername existiert bereits"));
            }

            UserResponseDto createdUser = authService.register(request);
            return ResponseEntity.status(201).body(createdUser);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Interner Serverfehler"));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user and get JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody UserRequestDto request) {
        return authService.loginWithJwt(request)
                .map(token -> ResponseEntity.ok(new AuthenticationResponseDto(request.getUsername(), token)))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String jwt = authHeader.substring(7);
        String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        Optional<UserResponseDto> userDtoOpt = authService.findByUsername(username);

        if (userDtoOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        UserResponseDto userDto = userDtoOpt.get();

        if (!"USER".equals(userDto.getRole()) && !"ADMIN".equals(userDto.getRole())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a user account")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> deleteUser(@PathVariable String username,
                                       @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Authentication check
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String tokenUsername;
        try {
            tokenUsername = jwtService.extractUsername(authHeader.substring(7));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        // Authorization check - users can only delete their own account or admins can delete any account
        Optional<UserResponseDto> currentUserOpt = authService.findByUsername(tokenUsername);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        UserResponseDto currentUser = currentUserOpt.get();
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        boolean isOwnAccount = tokenUsername.equals(username);

        if (!isAdmin && !isOwnAccount) {
            return ResponseEntity.status(403).build();
        }

        try {
            authService.deleteUser(username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete user"));
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserRequestDto updateRequest,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Authentication check
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String username;
        try {
            username = jwtService.extractUsername(authHeader.substring(7));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        try {
            UserResponseDto updatedUser = authService.updateUserProfile(username, updateRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Username already exists")) {
                return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update profile"));
        }
    }

    @PutMapping("/users/{username}/role")
    @Operation(summary = "Update user role (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Role updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid role"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> updateUserRole(@PathVariable String username,
                                           @RequestBody Map<String, String> roleRequest,
                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Authentication check
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String tokenUsername;
        try {
            tokenUsername = jwtService.extractUsername(authHeader.substring(7));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        // Authorization check - only admins can update roles
        Optional<UserResponseDto> currentUserOpt = authService.findByUsername(tokenUsername);
        if (currentUserOpt.isEmpty() || !"ADMIN".equals(currentUserOpt.get().getRole())) {
            return ResponseEntity.status(403).build();
        }

        String newRole = roleRequest.get("role");
        if (newRole == null || newRole.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Role is required"));
        }

        try {
            authService.updateUserRole(username, newRole);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Invalid role")) {
                return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update user role"));
        }
    }

    @GetMapping("/users")
    @Operation(summary = "Search and filter users (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required")
    })
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String search,
                                        @RequestParam(required = false) String role,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Authentication check
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String username;
        try {
            username = jwtService.extractUsername(authHeader.substring(7));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        // Authorization check - only admins can search users
        Optional<UserResponseDto> currentUserOpt = authService.findByUsername(username);
        if (currentUserOpt.isEmpty() || !"ADMIN".equals(currentUserOpt.get().getRole())) {
            return ResponseEntity.status(403).build();
        }

        try {
            List<UserResponseDto> users = authService.searchUsers(search, role, username);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to search users"));
        }
    }
}

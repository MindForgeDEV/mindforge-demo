package mindforge.controller;

import lombok.RequiredArgsConstructor;
import mindforge.dto.AuthResponseDto;
import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.service.AuthService;
import mindforge.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
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
    public ResponseEntity<AuthResponseDto> login(@RequestBody UserRequestDto request) {
        return authService.loginWithJwt(request)
                .map(token -> ResponseEntity.ok(new AuthResponseDto(request.getUsername(), token)))
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
}

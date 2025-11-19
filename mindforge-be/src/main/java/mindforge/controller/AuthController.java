package mindforge.controller;

import lombok.RequiredArgsConstructor;
import mindforge.dto.AuthResponseDto;
import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.service.AuthService;
import mindforge.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto request) {
    try {
      if (authService.findByUsername(request.getUsername()).isPresent()) {
        return ResponseEntity.status(409)
            .body(Map.of("error", "Benutzername existiert bereits"));
      }

      // User anlegen
      UserResponseDto createdUser = authService.register(request);

      // Erfolgreiche Response
      return ResponseEntity.status(201).body(createdUser);

    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(Map.of("error", "Interner Serverfehler"));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody UserRequestDto request) {
    return authService.loginWithJwt(request)
        .map(token -> ResponseEntity.ok(new AuthResponseDto(request.getUsername(), token)))
        .orElseGet(() -> ResponseEntity.status(401).build());
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> me(@RequestHeader("Authorization") String authHeader) {
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

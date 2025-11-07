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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto request) {
    try {
      UserResponseDto user = authService.register(request);
      return ResponseEntity.ok(user);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build(); // Username exists
    }
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody UserRequestDto request) {
    return authService.loginWithJwt(request)
        .map(token -> ResponseEntity.ok(new AuthResponseDto(request.getUsername(), token)))
        .orElseGet(() -> ResponseEntity.status(401).build()); // Invalid credentials
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
      return ResponseEntity.status(401).build(); // Invalid or expired token
    }

    Optional<UserResponseDto> userDtoOpt = authService.findByUsername(username);

    if (userDtoOpt.isEmpty()) {
      return ResponseEntity.status(404).build(); // User not found
    }

    UserResponseDto userDto = userDtoOpt.get();

    // Rolle USER oder ADMIN darf auf jeden Fall zugreifen
    if (!"USER".equals(userDto.getRole()) && !"ADMIN".equals(userDto.getRole())) {
      return ResponseEntity.status(403).build(); // Forbidden
    }

    return ResponseEntity.ok(userDto); // Zugriff erlaubt
  }
}

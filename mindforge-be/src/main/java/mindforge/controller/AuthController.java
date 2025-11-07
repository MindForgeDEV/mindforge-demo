package mindforge.controller;

import lombok.RequiredArgsConstructor;
import mindforge.dto.AuthResponseDto;
import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.service.AuthService;
import mindforge.service.JwtService;
import mindforge.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @PostMapping("/register")
  public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto request) {
    try {
      UserResponseDto user = authService.register(request);
      return ResponseEntity.ok(user);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
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
    if (authHeader == null || !authHeader.startsWith("Bearer "))
      return ResponseEntity.status(401).build();

    String jwt = authHeader.substring(7);

    String username;
    try {
      username = jwtService.extractUsername(jwt);
    } catch (Exception e) {
      return ResponseEntity.status(401).build(); // invalid, expired, manipulated token
    }

    return authService.findByUsername(username)
        .map(u -> ResponseEntity.ok(u))
        .orElse(ResponseEntity.status(404).build());
  }
}

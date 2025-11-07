package mindforge.controller;

import lombok.RequiredArgsConstructor;
import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto request) {
    try {
      UserResponseDto user = authService.register(request);
      return ResponseEntity.ok(user);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/login")
  public ResponseEntity<UserResponseDto> login(@RequestBody UserRequestDto request) {
    return authService.login(request)
        .map(user -> ResponseEntity.ok(user))
        .orElseGet(() -> ResponseEntity.status(401)
            .body(null));
  }
}

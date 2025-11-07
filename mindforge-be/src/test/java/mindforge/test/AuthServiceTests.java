package mindforge.test;

import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTests {

  @Autowired
  private AuthService authService;

  @Test
  void registerAndLoginWorks() {
    // DTO erstellen
    UserRequestDto request = UserRequestDto.builder()
        .username("testuser")
        .password("password")
        .build();

    // Registrierung
    UserResponseDto registered = authService.register(request);
    assertThat(registered.getUsername()).isEqualTo("testuser");
    assertThat(registered.getRole()).isEqualTo("USER");

    // Login
    UserResponseDto loggedIn = authService.login(request).orElse(null);
    assertThat(loggedIn).isNotNull();
    assertThat(loggedIn.getUsername()).isEqualTo("testuser");
  }

  @Test
  void loginFailsWithWrongPassword() {
    UserRequestDto request = UserRequestDto.builder()
        .username("wronguser")
        .password("badpass")
        .build();

    assertThat(authService.login(request)).isEmpty();
  }
}


package mindforge.test;

import mindforge.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceUnitTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    String secret = "supersecretkeysupersecretkey12341234";
    long expirationMillis = 3600_000; // 1 Stunde
    jwtService = new JwtService(secret, expirationMillis);
  }

  @Test
  void generateToken_and_extractUsername_workCorrectly() {
    String username = "testuser";

    String token = jwtService.generateToken(username);

    assertThat(token).isNotNull().isNotEmpty();

    String extractedUsername = jwtService.extractUsername(token);

    assertThat(extractedUsername).isEqualTo(username);
  }
}

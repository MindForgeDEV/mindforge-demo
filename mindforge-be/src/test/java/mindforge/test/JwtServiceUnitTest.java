
package mindforge.test;

import mindforge.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.security.Keys;

class JwtServiceUnitTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    String secret = "supersecretkeysupersecretkey12341234";
    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    long expirationMillis = 3600_000; // 1 Stunde
    long refreshExpirationMillis = 604800000; // 7 Tage
    jwtService = new JwtService(secretKey, expirationMillis, refreshExpirationMillis);
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

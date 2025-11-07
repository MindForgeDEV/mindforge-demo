package mindforge.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Configuration
public class JwtTestUtil {

  private final SecretKey key;
  private final long expirationMs;

  public JwtTestUtil(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration}") long expirationMs) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // sicherer Schlüssel
    this.expirationMs = expirationMs;
  }

  // JWT für einen Benutzernamen generieren
  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }
}

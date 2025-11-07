package mindforge.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class JwtTestUtil {

  @Value("${jwt.secret:secret-key-for-tests}") // default Secret für Tests
  private String secret;

  @Value("${jwt.expiration:3600000}") // 1 Stunde
  private long expirationMs;

  // JWT für einen Benutzernamen generieren
  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }
}

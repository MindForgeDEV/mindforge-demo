
package mindforge.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

@Service
public class JwtService {
  private final String secret;
  private final long expirationMillis;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public JwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration}") long expirationMillis) {
    this.secret = secret;
    this.expirationMillis = expirationMillis;
  }

  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
        .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser()
        .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public String extractTokenFromJson(String json) throws Exception {
    JsonNode node = objectMapper.readTree(json);
    // assuming your login endpoint returns {"token": "...", "username": "..."}
    return node.get("token").asText();
  }
}

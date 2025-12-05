package mindforge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

  private final SecretKey key;
  private final long expirationMillis;
  private final long refreshExpirationMillis;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public JwtService(
      SecretKey jwtSecretKey,
      @Value("${jwt.expiration}") long expirationMillis,
      @Value("${jwt.refresh-expiration:604800000}") long refreshExpirationMillis) { // 7 days default

    this.key = jwtSecretKey;
    this.expirationMillis = expirationMillis;
    this.refreshExpirationMillis = refreshExpirationMillis;
  }

  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
        .signWith(key, SignatureAlgorithm.HS256) // konsistenter Algorithmus
        .compact();
  }

  public String generateRefreshToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMillis))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }

  public String extractTokenFromJson(String json) throws Exception {
    JsonNode node = objectMapper.readTree(json);
    return node.get("token").asText();
  }
}

package mindforge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.security.Keys;

@Configuration
@Profile("test")
public class JwtTestConfig {

    @Bean
    public SecretKey jwtSecretKey() {
        String secret = "verylongrandomstringwithatleast32charactersforjwttesting!!!";
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
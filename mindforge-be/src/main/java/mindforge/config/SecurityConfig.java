package mindforge.config;

import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import mindforge.config.RateLimitingFilter;

@Configuration
@Profile("!test")
@EnableMethodSecurity
public class SecurityConfig {

  @Value("${frontend.url}")
  private String frontendUrl;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigSource, JwtDecoder jwtDecoder, RateLimitingFilter rateLimitingFilter)
      throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigSource))
        .addFilterBefore(rateLimitingFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health", "/api/health", "/auth/register", "/auth/login").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)))
        .headers(headers -> headers
            .frameOptions().deny()
            .contentTypeOptions());
    return http.build();
  }

  @Bean
  public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
    return NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();
  }

  @Bean
  @Primary
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOrigins(List.of(frontendUrl));
    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    corsConfig.setAllowedHeaders(List.of("*"));
    corsConfig.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
  }
}

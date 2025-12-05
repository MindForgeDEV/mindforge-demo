package mindforge.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Builder.Default
  private String role = "USER";

  @Builder.Default
  @Column(name = "failed_login_attempts")
  private Integer failedLoginAttempts = 0;

  @Column(name = "locked_until")
  private java.time.LocalDateTime lockedUntil;

  @Builder.Default
  @Column(name = "account_locked")
  private Boolean accountLocked = false;

  @Column(name = "last_login_attempt")
  private java.time.LocalDateTime lastLoginAttempt;

  @Column(name = "created_at")
  private java.time.LocalDateTime createdAt;

  @Column(name = "updated_at")
  private java.time.LocalDateTime updatedAt;
}

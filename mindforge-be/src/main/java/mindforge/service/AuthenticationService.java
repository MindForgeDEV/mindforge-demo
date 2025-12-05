package mindforge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.model.User;
import mindforge.repository.UserRepository;
import mindforge.service.PasswordValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final PasswordValidationService passwordValidationService;

  @Value("${security.account-lockout.max-attempts:5}")
  private int maxFailedLoginAttempts;

  @Value("${security.account-lockout.duration-minutes:30}")
  private int lockoutDurationMinutes;

  public UserResponseDto register(UserRequestDto request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      log.warn("Registration failed: Username '{}' already exists", request.getUsername());
      throw new IllegalArgumentException("Username already exists");
    }

    // Validate password strength
    var passwordValidation = passwordValidationService.validatePassword(request.getPassword());
    if (!passwordValidation.isValid()) {
      log.warn("Registration failed: Weak password for user '{}': {}", request.getUsername(), passwordValidation.getMessage());
      throw new IllegalArgumentException(passwordValidation.getMessage());
    }

    User user = User.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .role("USER")
        .failedLoginAttempts(0)
        .accountLocked(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    userRepository.save(user);

    log.info("User registered successfully: {}", request.getUsername());

    return UserResponseDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .role(user.getRole())
        .build();
  }

  public Optional<UserResponseDto> login(UserRequestDto request) {
    String username = request.getUsername();
    User user = userRepository.findByUsername(username).orElse(null);

    if (user == null) {
      log.warn("Login failed: User '{}' not found", username);
      return Optional.empty();
    }

    // Check if account is locked
    if (isAccountLocked(user)) {
      log.warn("Login failed: Account locked for user '{}'", username);
      return Optional.empty();
    }

    boolean passwordValid = passwordMatches(request.getPassword(), user.getPassword());

    if (passwordValid) {
      // Successful login - reset failed attempts
      user.setFailedLoginAttempts(0);
      user.setLastLoginAttempt(LocalDateTime.now());
      user.setLockedUntil(null);
      user.setAccountLocked(false);
      user.setUpdatedAt(LocalDateTime.now());
      userRepository.save(user);

      log.info("Login successful for user: {}", username);

      return Optional.of(UserResponseDto.builder()
          .id(user.getId())
          .username(user.getUsername())
          .role(user.getRole())
          .build());
    } else {
      // Failed login - increment attempts
      int newAttempts = user.getFailedLoginAttempts() + 1;
      user.setFailedLoginAttempts(newAttempts);
      user.setLastLoginAttempt(LocalDateTime.now());

      if (newAttempts >= maxFailedLoginAttempts) {
        // Lock the account
        user.setAccountLocked(true);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(lockoutDurationMinutes));
        log.warn("Account locked for user '{}' after {} failed attempts", username, newAttempts);
      }

      user.setUpdatedAt(LocalDateTime.now());
      userRepository.save(user);

      log.warn("Login failed for user '{}' (attempt {}/{})", username, newAttempts, maxFailedLoginAttempts);
      return Optional.empty();
    }
  }

  public Optional<String> loginWithJwt(UserRequestDto request) {
    return userRepository.findByUsername(request.getUsername())
        .filter(u -> passwordMatches(request.getPassword(), u.getPassword()))
        .map(u -> jwtService.generateToken(u.getUsername()));
  }

  public Optional<UserResponseDto> findByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(u -> UserResponseDto.builder()
            .id(u.getId())
            .username(u.getUsername())
            .role(u.getRole())
            .build());
  }

  public String encodePassword(String raw) {
    return passwordEncoder.encode(raw);
  }

  public boolean passwordMatches(String raw, String hashed) {
    return passwordEncoder.matches(raw, hashed);
  }

  private boolean isAccountLocked(User user) {
    if (user.getAccountLocked() == null) return false;
    if (!user.getAccountLocked()) return false;

    // Check if lockout period has expired
    if (user.getLockedUntil() != null && LocalDateTime.now().isAfter(user.getLockedUntil())) {
      // Lockout expired, unlock the account
      user.setAccountLocked(false);
      user.setLockedUntil(null);
      user.setFailedLoginAttempts(0);
      user.setUpdatedAt(LocalDateTime.now());
      userRepository.save(user);
      return false;
    }

    return true;
  }

  public void deleteUser(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    userRepository.delete(user);
  }

  public UserResponseDto updateUserProfile(String currentUsername, UserRequestDto updateRequest) {
    User user = userRepository.findByUsername(currentUsername)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Check if username is being changed and if it's available
    if (!currentUsername.equals(updateRequest.getUsername()) &&
        userRepository.existsByUsername(updateRequest.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }

    // Update username if changed
    if (!currentUsername.equals(updateRequest.getUsername())) {
      user.setUsername(updateRequest.getUsername());
    }

    // Update password if provided
    if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
      user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
    }

    userRepository.save(user);

    return UserResponseDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .role(user.getRole())
        .build();
  }

  public void updateUserRole(String username, String newRole) {
    if (!"USER".equals(newRole) && !"ADMIN".equals(newRole)) {
      throw new IllegalArgumentException("Invalid role. Must be USER or ADMIN");
    }

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    user.setRole(newRole);
    userRepository.save(user);
  }

  public List<UserResponseDto> searchUsers(String searchTerm, String roleFilter, String currentUsername) {
    List<User> users;

    if (roleFilter != null && !roleFilter.trim().isEmpty()) {
      if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        users = userRepository.findByRoleAndUsernameContainingIgnoreCase(roleFilter, searchTerm);
      } else {
        users = userRepository.findByRole(roleFilter);
      }
    } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
      users = userRepository.findByUsernameContainingIgnoreCase(searchTerm);
    } else {
      users = userRepository.findAll();
    }

    return users.stream()
        .filter(user -> !user.getUsername().equals(currentUsername)) // Don't include current user in results
        .map(user -> UserResponseDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .role(user.getRole())
            .build())
        .toList();
  }
}

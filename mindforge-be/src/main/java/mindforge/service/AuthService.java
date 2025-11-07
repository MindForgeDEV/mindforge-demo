package mindforge.service;

import lombok.RequiredArgsConstructor;
import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.model.User;
import mindforge.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public UserResponseDto register(UserRequestDto request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }

    User user = User.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .role("USER")
        .build();

    userRepository.save(user);

    return UserResponseDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .role(user.getRole())
        .build();
  }

  public Optional<UserResponseDto> login(UserRequestDto request) {
    return userRepository.findByUsername(request.getUsername())
        .filter(u -> passwordMatches(request.getPassword(), u.getPassword()))
        .map(u -> UserResponseDto.builder()
            .id(u.getId())
            .username(u.getUsername())
            .role(u.getRole())
            .build());
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
}

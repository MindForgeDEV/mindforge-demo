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

  public UserResponseDto register(UserRequestDto request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new RuntimeException("Username already exists");
    }

    User user = User.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
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
        .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
        .map(u -> UserResponseDto.builder()
            .id(u.getId())
            .username(u.getUsername())
            .role(u.getRole())
            .build());
  }
}

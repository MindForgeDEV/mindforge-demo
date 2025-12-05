package mindforge.test;

import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.model.User;
import mindforge.repository.UserRepository;
import mindforge.service.AuthenticationService;
import mindforge.service.PasswordValidationService;
import mindforge.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthenticationServiceUnitTest {

  private AuthenticationService authService;
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private JwtService jwtService;
  private PasswordValidationService passwordValidationService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = new BCryptPasswordEncoder();
    jwtService = mock(JwtService.class);
    passwordValidationService = mock(PasswordValidationService.class);

    // Mock password validation to return valid result
    PasswordValidationService.PasswordValidationResult validResult =
        new PasswordValidationService.PasswordValidationResult(true, PasswordValidationService.PasswordStrength.STRONG, "Strong password");
    when(passwordValidationService.validatePassword(anyString())).thenReturn(validResult);

    authService = new AuthenticationService(userRepository, passwordEncoder, jwtService, passwordValidationService);
  }

  @Test
  void register_createsUser_whenUsernameNotExists() {
    UserRequestDto request = UserRequestDto.builder()
        .username("newuser")
        .password("password")
        .build();

    when(userRepository.existsByUsername("newuser")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    UserResponseDto response = authService.register(request);

    assertThat(response.getUsername()).isEqualTo("newuser");
    assertThat(response.getRole()).isEqualTo("USER");
    verify(userRepository).save(any(User.class));
  }

  @Test
  void registerFails_whenUsernameExists() {
    UserRequestDto request = UserRequestDto.builder()
        .username("dupuser")
        .password("password")
        .build();

    when(userRepository.existsByUsername("dupuser")).thenReturn(true);

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Username already exists");
  }

  @Test
  void login_returnsUser_whenCredentialsCorrect() {
    UserRequestDto request = UserRequestDto.builder()
        .username("existing")
        .password("password")
        .build();

    User existingUser = User.builder()
        .username("existing")
        .password(authService.encodePassword("password"))
        .role("USER")
        .build();

    when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

    Optional<UserResponseDto> login = authService.login(request);

    assertThat(login).isPresent();
    assertThat(login.get().getUsername()).isEqualTo("existing");
  }

  @Test
  void login_returnsEmpty_whenPasswordIncorrect() {
    UserRequestDto request = UserRequestDto.builder()
        .username("existing")
        .password("wrong")
        .build();

    User existingUser = User.builder()
        .username("existing")
        .password(authService.encodePassword("password"))
        .role("USER")
        .build();

    when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

    Optional<UserResponseDto> login = authService.login(request);

    assertThat(login).isEmpty();
  }
}

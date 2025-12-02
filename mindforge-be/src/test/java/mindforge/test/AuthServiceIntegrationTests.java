package mindforge.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindforge.dto.AuthResponseDto;
import mindforge.dto.UserRequestDto;
import mindforge.dto.UserResponseDto;
import mindforge.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthServiceIntegrationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private JwtService jwtService;

  @BeforeEach
  void checkDatabase() {
    try {
      jdbcTemplate.execute("SELECT 1");
    } catch (Exception e) {
      throw new RuntimeException("Datenbank nicht erreichbar", e);
    }
  }

  @Test
  void register_and_login_works() throws Exception {
    UserRequestDto request = UserRequestDto.builder()
        .username("testuser")
        .password("password123")
        .build();

    // Registrierung
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testuser"));

    // Login
    MvcResult loginResult = mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    AuthResponseDto authResponse = objectMapper.readValue(
        loginResult.getResponse().getContentAsString(),
        AuthResponseDto.class);
    String token = authResponse.getToken();
    assertThat(token).isNotEmpty();

    // /me Endpoint testen mit JWT
    MvcResult meResult = mockMvc.perform(get("/auth/me")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    UserResponseDto meDto = objectMapper.readValue(
        meResult.getResponse().getContentAsString(),
        UserResponseDto.class);
    assertThat(meDto.getUsername()).isEqualTo("testuser");
  }

  @Test
  void register_fails_with_duplicate_username() throws Exception {
    UserRequestDto request = UserRequestDto.builder()
        .username("duplicateUser")
        .password("password123")
        .build();

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void login_fails_with_wrong_password() throws Exception {
    UserRequestDto registerRequest = UserRequestDto.builder()
        .username("loginUser")
        .password("correctPass")
        .build();

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    UserRequestDto wrongLogin = UserRequestDto.builder()
        .username("loginUser")
        .password("wrongPass")
        .build();

    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(wrongLogin)))
        .andExpect(status().isUnauthorized());
  }
}

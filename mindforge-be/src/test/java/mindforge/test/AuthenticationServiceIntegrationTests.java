package mindforge.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindforge.dto.AuthenticationResponseDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthenticationServiceIntegrationTests {

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
        .password("VeryStrongPass123!")
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

    AuthenticationResponseDto authResponse = objectMapper.readValue(
        loginResult.getResponse().getContentAsString(),
        AuthenticationResponseDto.class);
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
    void update_user_profile_works() throws Exception {
        // Register a user
        UserRequestDto registerRequest = UserRequestDto.builder()
            .username("profileuser")
            .password("StrongPass123")
            .build();

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated());

        // Login to get token
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andReturn();

        AuthenticationResponseDto authResponse = objectMapper.readValue(
            loginResult.getResponse().getContentAsString(),
            AuthenticationResponseDto.class);
        String token = authResponse.getToken();

        // Update profile (change password)
        UserRequestDto updateRequest = UserRequestDto.builder()
            .username("profileuser")
            .password("newpassword123")
            .build();

        mockMvc.perform(put("/auth/profile")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("profileuser"));

        // Verify login works with new password
        UserRequestDto newLoginRequest = UserRequestDto.builder()
            .username("profileuser")
            .password("newpassword123")
            .build();

        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newLoginRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void update_username_works() throws Exception {
        // Register a user
    String baseUsername = "oldusername_" + System.currentTimeMillis();
    UserRequestDto registerRequest = UserRequestDto.builder()
        .username(baseUsername)
        .password("StrongPass123")
        .build();

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated());

        // Login to get token
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andReturn();

        AuthenticationResponseDto authResponse = objectMapper.readValue(
            loginResult.getResponse().getContentAsString(),
            AuthenticationResponseDto.class);
        String token = authResponse.getToken();

        // Update username
        String newUsername = "newusername_" + System.currentTimeMillis();
        UserRequestDto updateRequest = UserRequestDto.builder()
            .username(newUsername)
            .password("")
            .build();

        mockMvc.perform(put("/auth/profile")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(newUsername));

        // Verify login works with new username
        UserRequestDto newLoginRequest = UserRequestDto.builder()
            .username(newUsername)
            .password("StrongPass123")
            .build();

        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newLoginRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void delete_user_works() throws Exception {
        // Register a user
        UserRequestDto registerRequest = UserRequestDto.builder()
            .username("deleteuser")
            .password("StrongPass123")
            .build();

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isCreated());

        // Login to get token
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andReturn();

        AuthenticationResponseDto authResponse = objectMapper.readValue(
            loginResult.getResponse().getContentAsString(),
            AuthenticationResponseDto.class);
        String token = authResponse.getToken();

        // Delete user
        mockMvc.perform(delete("/auth/users/deleteuser")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        // Verify user is deleted (login should fail)
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void register_fails_with_duplicate_username() throws Exception {
    UserRequestDto request = UserRequestDto.builder()
        .username("duplicateUser")
        .password("StrongPass123")
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

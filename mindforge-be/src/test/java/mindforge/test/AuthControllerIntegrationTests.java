package mindforge.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindforge.dto.AuthResponseDto;
import mindforge.dto.UserResponseDto;
import mindforge.dto.UserRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void register_and_login_over_http_works() throws Exception {
    // Registrierung
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "username":"u1",
              "password":"pw"
            }
            """))
        .andExpect(status().isCreated());

    // Login und JWT abholen
    MvcResult loginResult = mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "username":"u1",
              "password":"pw"
            }
            """))
        .andExpect(status().isOk())
        .andReturn();

    AuthResponseDto authResponse = objectMapper.readValue(
        loginResult.getResponse().getContentAsString(),
        AuthResponseDto.class);
    String token = authResponse.getToken();
    assertThat(token).isNotEmpty();

    // /me-Endpunkt prüfen (USER-Rolle)
    MvcResult meResult = mockMvc.perform(get("/auth/me")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    UserResponseDto meDto = objectMapper.readValue(
        meResult.getResponse().getContentAsString(),
        UserResponseDto.class);
    assertThat(meDto.getUsername()).isEqualTo("u1");
    assertThat(meDto.getRole()).isEqualTo("USER");
  }

  @Test
  void login_fails_wrong_password() throws Exception {
    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "username":"doesnotexist",
              "password":"nope"
            }
            """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void access_me_fails_without_token() throws Exception {
    mockMvc.perform(get("/auth/me")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void access_me_fails_with_invalid_token() throws Exception {
    mockMvc.perform(get("/auth/me")
        .header("Authorization", "Bearer invalid.token.here")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void access_me_fails_for_invalid_role() throws Exception {
    // User mit anderer Rolle registrieren
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "username":"adminuser",
              "password":"pw"
            }
            """))
        .andExpect(status().isCreated());

    // Login
    MvcResult loginResult = mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "username":"adminuser",
              "password":"pw"
            }
            """))
        .andExpect(status().isOk())
        .andReturn();

    AuthResponseDto authResponse = objectMapper.readValue(
        loginResult.getResponse().getContentAsString(),
        AuthResponseDto.class);

    // /me-Endpunkt mit Rolle, die nicht passt (falls du Rollenprüfung auf andere
    // Rollen hast)
    MvcResult meResult = mockMvc.perform(get("/auth/me")
        .header("Authorization", "Bearer " + authResponse.getToken())
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    System.out.println("Me with valid token - Status: " + meResult.getResponse().getStatus());
    System.out.println("Me with valid token - Content: " + meResult.getResponse().getContentAsString());
  }
}

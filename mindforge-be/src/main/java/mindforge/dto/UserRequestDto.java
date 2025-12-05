package mindforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration/login request")
public class UserRequestDto {
  
  @Schema(description = "Username of the user", example = "john123")
  private String username;

  @Schema(description = "Password of the user", example = "secret123")
  private String password;

  @Schema(description = "Email of the user", example = "john@example.com")
  private String email;

  @Schema(description = "First name of the user", example = "John")
  private String firstName;

  @Schema(description = "Last name of the user", example = "Doe")
  private String lastName;

  @Schema(description = "Avatar URL of the user", example = "https://example.com/avatar.jpg")
  private String avatarUrl;
}

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
}

package mindforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(description = "Response with user details")
public class UserResponseDto {

     @NotNull
     @Schema(description = "ID of the user", example = "1")
     private Long id;

     @NotBlank
     @Schema(description = "Username of the user", example = "john123")
     private String username;

     @NotBlank
     @Schema(description = "Role of the user", example = "USER")
     private String role;

     @Schema(description = "Email of the user", example = "john@example.com")
     private String email;

     @Schema(description = "First name of the user", example = "John")
     private String firstName;

     @Schema(description = "Last name of the user", example = "Doe")
     private String lastName;

      @Schema(description = "Avatar URL of the user", example = "https://example.com/avatar.jpg")
      private String avatarUrl;

      @Schema(description = "Whether the account is locked", example = "false")
      private Boolean accountLocked;

      @Schema(description = "Number of failed login attempts", example = "0")
      private Integer failedLoginAttempts;

      @Schema(description = "Account creation timestamp")
      private java.time.LocalDateTime createdAt;

      @Schema(description = "Account last update timestamp")
      private java.time.LocalDateTime updatedAt;
}

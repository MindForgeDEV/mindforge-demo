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
}

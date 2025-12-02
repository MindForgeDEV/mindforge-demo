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
@Schema(description = "Response with user details")
public class UserResponseDto {

    @Schema(description = "ID of the user", example = "1")
    private Long id;

    @Schema(description = "Username of the user", example = "john123")
    private String username;

    @Schema(description = "Role of the user", example = "USER")
    private String role;
}

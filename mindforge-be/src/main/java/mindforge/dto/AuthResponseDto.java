package mindforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after successful login")
public class AuthResponseDto {

    @NotBlank
    @Schema(description = "Username of the user", example = "john123")
    private String username;

    @NotBlank
    @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthResponseDto that = (AuthResponseDto) o;
        return Objects.equals(username, that.username) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, token);
    }

    @Override
    public String toString() {
        return "AuthResponseDto{" +
                "username='" + username + '\'' +
                ", token='[PROTECTED]'" +
                '}';
    }
}

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
@Schema(description = "Project data transfer object")
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerUsername;
    private Boolean isPublic;
}
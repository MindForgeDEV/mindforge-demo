package mindforge.controller;

import lombok.RequiredArgsConstructor;
import mindforge.dto.ProjectDto;
import mindforge.service.JwtService;
import mindforge.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProjectDto>> getPublicProjects() {
        return ResponseEntity.ok(projectService.getPublicProjects());
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProjectDto>> getMyProjects(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = extractUsername(authHeader);
        return ResponseEntity.ok(projectService.getUserProjects(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto,
                                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = extractUsername(authHeader);
        ProjectDto created = projectService.createProject(username, projectDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id,
                                                   @RequestBody ProjectDto projectDto,
                                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = extractUsername(authHeader);
        ProjectDto updated = projectService.updateProject(username, id, projectDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id,
                                             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = extractUsername(authHeader);
        projectService.deleteProject(username, id);
        return ResponseEntity.noContent().build();
    }

    private String extractUsername(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        return jwtService.extractUsername(token);
    }
}
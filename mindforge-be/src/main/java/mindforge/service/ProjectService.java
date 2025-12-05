package mindforge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mindforge.dto.ProjectDto;
import mindforge.model.Project;
import mindforge.model.User;
import mindforge.repository.ProjectRepository;
import mindforge.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectDto> getPublicProjects() {
        return projectRepository.findByIsPublicTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectDto> getUserProjects(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return projectRepository.findByOwner(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return toDto(project);
    }

    @PreAuthorize("hasRole('USER')")
    public ProjectDto createProject(String username, ProjectDto projectDto) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Project project = Project.builder()
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .owner(owner)
                .isPublic(projectDto.getIsPublic() != null ? projectDto.getIsPublic() : false)
                .build();

        project = projectRepository.save(project);
        log.info("Project created: {} by {}", project.getName(), username);
        return toDto(project);
    }

    @PreAuthorize("hasRole('USER')")
    public ProjectDto updateProject(String username, Long projectId, ProjectDto projectDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Check ownership
        if (!project.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Not authorized to update this project");
        }

        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setIsPublic(projectDto.getIsPublic() != null ? projectDto.getIsPublic() : project.getIsPublic());

        project = projectRepository.save(project);
        log.info("Project updated: {}", project.getName());
        return toDto(project);
    }

    @PreAuthorize("hasRole('USER')")
    public void deleteProject(String username, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Check ownership
        if (!project.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Not authorized to delete this project");
        }

        projectRepository.delete(project);
        log.info("Project deleted: {}", project.getName());
    }

    private ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwner().getId())
                .ownerUsername(project.getOwner().getUsername())
                .isPublic(project.getIsPublic())
                .build();
    }
}
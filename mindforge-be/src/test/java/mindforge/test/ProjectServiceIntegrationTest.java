package mindforge.test;

import mindforge.dto.ProjectDto;
import mindforge.model.Project;
import mindforge.model.User;
import mindforge.repository.ProjectRepository;
import mindforge.repository.UserRepository;
import mindforge.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProjectServiceIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Clean up
        projectRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testUser = User.builder()
                .username("testuser")
                .password("hashedpassword")
                .role("USER")
                .build();
        testUser = userRepository.save(testUser);

        adminUser = User.builder()
                .username("admin")
                .password("hashedpassword")
                .role("ADMIN")
                .build();
        adminUser = userRepository.save(adminUser);

        // Ensure IDs are set
        assertThat(testUser.getId()).isNotNull();
        assertThat(adminUser.getId()).isNotNull();
    }

    @Test
    void shouldCreateProjectSuccessfully() {
        // Given
        ProjectDto projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("A test project")
                .isPublic(true)
                .build();

        // When
        ProjectDto created = projectService.createProject(testUser.getUsername(), projectDto);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Project");
        assertThat(created.getDescription()).isEqualTo("A test project");
        assertThat(created.getIsPublic()).isTrue();
        assertThat(created.getOwnerUsername()).isEqualTo(testUser.getUsername());

        // Verify in database
        Project saved = projectRepository.findById(created.getId()).orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getOwner().getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void shouldGetUserProjects() {
        // Given
        Project project1 = Project.builder()
                .name("Project 1")
                .description("Description 1")
                .owner(testUser)
                .isPublic(false)
                .build();
        projectRepository.save(project1);

        Project project2 = Project.builder()
                .name("Project 2")
                .description("Description 2")
                .owner(testUser)
                .isPublic(true)
                .build();
        projectRepository.save(project2);

        // When
        List<ProjectDto> userProjects = projectService.getUserProjects(testUser.getUsername());

        // Then
        assertThat(userProjects).hasSize(2);
        assertThat(userProjects).extracting("name").contains("Project 1", "Project 2");
    }

    @Test
    void shouldGetPublicProjects() {
        // Given
        Project publicProject = Project.builder()
                .name("Public Project")
                .description("Public description")
                .owner(testUser)
                .isPublic(true)
                .build();
        projectRepository.save(publicProject);

        Project privateProject = Project.builder()
                .name("Private Project")
                .description("Private description")
                .owner(testUser)
                .isPublic(false)
                .build();
        projectRepository.save(privateProject);

        // When
        List<ProjectDto> publicProjects = projectService.getPublicProjects();

        // Then
        assertThat(publicProjects).hasSize(1);
        assertThat(publicProjects.get(0).getName()).isEqualTo("Public Project");
    }

    @Test
    void shouldUpdateProjectSuccessfully() {
        // Given
        Project project = Project.builder()
                .name("Original Name")
                .description("Original description")
                .owner(testUser)
                .isPublic(false)
                .build();
        project = projectRepository.save(project);

        ProjectDto updateDto = ProjectDto.builder()
                .name("Updated Name")
                .description("Updated description")
                .isPublic(true)
                .build();

        // When
        ProjectDto updated = projectService.updateProject(testUser.getUsername(), project.getId(), updateDto);

        // Then
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getIsPublic()).isTrue();
    }

    @Test
    void shouldNotUpdateProjectOfDifferentUser() {
        // Given
        Project project = Project.builder()
                .name("Test Project")
                .owner(testUser)
                .build();
        project = projectRepository.save(project);

        ProjectDto updateDto = ProjectDto.builder()
                .name("Hacked Name")
                .build();

        final Long projectId = project.getId();

        // When/Then
        assertThatThrownBy(() -> projectService.updateProject(adminUser.getUsername(), projectId, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not authorized to update this project");
    }

    @Test
    void shouldDeleteProjectSuccessfully() {
        // Given
        Project project = Project.builder()
                .name("Test Project")
                .owner(testUser)
                .build();
        project = projectRepository.save(project);

        // When
        projectService.deleteProject(testUser.getUsername(), project.getId());

        // Then
        assertThat(projectRepository.findById(project.getId())).isEmpty();
    }

    @Test
    void shouldNotDeleteProjectOfDifferentUser() {
        // Given
        Project project = Project.builder()
                .name("Test Project")
                .owner(testUser)
                .build();
        project = projectRepository.save(project);

        final Long projectId = project.getId();

        // When/Then
        assertThatThrownBy(() -> projectService.deleteProject(adminUser.getUsername(), projectId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not authorized to delete this project");
    }
}
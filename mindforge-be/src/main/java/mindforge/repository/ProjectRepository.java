package mindforge.repository;

import mindforge.model.Project;
import mindforge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner(User owner);
    List<Project> findByIsPublicTrue();
    List<Project> findByOwnerAndIsPublic(User owner, boolean isPublic);
}
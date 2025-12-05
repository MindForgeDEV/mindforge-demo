package mindforge.repository;

import mindforge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
  boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);

  List<User> findByRole(String role);

  @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  List<User> findByUsernameContainingIgnoreCase(@Param("searchTerm") String searchTerm);

  @Query("SELECT u FROM User u WHERE u.role = :role AND LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  List<User> findByRoleAndUsernameContainingIgnoreCase(@Param("role") String role, @Param("searchTerm") String searchTerm);
}

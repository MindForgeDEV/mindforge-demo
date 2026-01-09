package mindforge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mindforge.dto.UserResponseDto;
import mindforge.model.User;
import mindforge.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate role
        if (!"USER".equals(newRole) && !"ADMIN".equals(newRole)) {
            throw new IllegalArgumentException("Invalid role. Must be USER or ADMIN");
        }

        user.setRole(newRole);
        user = userRepository.save(user);

        log.info("User role updated: {} -> {}", user.getUsername(), newRole);
        return toDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Prevent admin from deleting themselves
        if ("ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("Cannot delete admin users");
        }

        userRepository.delete(user);
        log.info("User deleted: {}", user.getUsername());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto lockUserAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setAccountLocked(true);
        user = userRepository.save(user);

        log.info("User account locked: {}", user.getUsername());
        return toDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto unlockUserAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user = userRepository.save(user);

        log.info("User account unlocked: {}", user.getUsername());
        return toDto(user);
    }

    private UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .accountLocked(user.getAccountLocked())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
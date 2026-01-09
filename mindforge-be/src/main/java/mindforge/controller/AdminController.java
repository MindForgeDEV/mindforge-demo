package mindforge.controller;

import lombok.RequiredArgsConstructor;
import mindforge.dto.UserResponseDto;
import mindforge.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(@PathVariable Long userId,
                                                         @RequestParam String role) {
        return ResponseEntity.ok(adminService.updateUserRole(userId, role));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/lock")
    public ResponseEntity<UserResponseDto> lockUserAccount(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.lockUserAccount(userId));
    }

    @PostMapping("/users/{userId}/unlock")
    public ResponseEntity<UserResponseDto> unlockUserAccount(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.unlockUserAccount(userId));
    }
}
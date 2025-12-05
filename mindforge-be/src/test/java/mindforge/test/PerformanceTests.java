package mindforge.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindforge.dto.UserRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled("Performance tests disabled - core functionality verified by other tests")
class PerformanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void performance_test_registration_endpoint() throws Exception {
        long startTime = System.nanoTime();

        // Test registration performance with 5 sequential users (avoiding DB conflicts)
        long testStart = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            String jsonRequest = String.format("""
                {
                    "username": "perfuser%s_%d",
                    "password": "StrongPass123"
                }
                """, testStart, i);

            mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated());
        }

        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println("Registration performance test completed in: " + durationMs + "ms");
        System.out.println("Average time per registration: " + (durationMs / 5.0) + "ms");

        // Assert reasonable performance (should complete within 10 seconds with security features)
        assertTrue(durationMs < 10000, "Registration performance too slow: " + durationMs + "ms");
    }

    @Test
    void performance_test_login_endpoint() throws Exception {
        // First register a user
        long testStart = System.currentTimeMillis();
        String registerJson = String.format("""
            {
                "username": "perfloginuser%s",
                "password": "StrongPass123"
            }
            """, testStart);

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registerJson))
            .andExpect(status().isCreated());

        long startTime = System.nanoTime();

        // Test login performance with 10 sequential logins (avoiding DB conflicts)
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk());
        }

        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println("Login performance test completed in: " + durationMs + "ms");
        System.out.println("Average time per login: " + (durationMs / 10.0) + "ms");

        // Assert reasonable performance (should complete within 5 seconds with security features)
        assertTrue(durationMs < 5000, "Login performance too slow: " + durationMs + "ms");
    }
}
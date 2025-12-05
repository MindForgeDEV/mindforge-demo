package mindforge.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import mindforge.dto.UserRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PerformanceRegressionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PERFORMANCE_BASELINE_FILE = "performance-baseline.json";

    @Test
    void performance_regression_test_auth_endpoints() throws Exception {
        Map<String, Long> currentMetrics = new HashMap<>();
        Map<String, Long> baselineMetrics = loadBaselineMetrics();

        // Test registration performance
        long registrationTime = measureEndpointPerformance("/auth/register", createTestUser("reg"));
        currentMetrics.put("registration", registrationTime);

        // Test login performance (need to register first)
        UserRequestDto loginUser = createTestUser("login");
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginUser)))
            .andExpect(status().isCreated());

        long loginTime = measureEndpointPerformance("/auth/login", loginUser);
        currentMetrics.put("login", loginTime);

        // Save current metrics as new baseline if no baseline exists
        if (baselineMetrics.isEmpty()) {
            saveBaselineMetrics(currentMetrics);
            System.out.println("Performance baseline established:");
            currentMetrics.forEach((key, value) ->
                System.out.println(key + ": " + value + "ms"));
            return;
        }

        // Check for performance regressions (allow 20% degradation)
        boolean hasRegression = false;
        for (Map.Entry<String, Long> entry : currentMetrics.entrySet()) {
            String endpoint = entry.getKey();
            long currentTime = entry.getValue();
            long baselineTime = baselineMetrics.getOrDefault(endpoint, currentTime);

            double degradation = ((double) (currentTime - baselineTime) / baselineTime) * 100;

            System.out.println(endpoint + " performance:");
            System.out.println("  Current: " + currentTime + "ms");
            System.out.println("  Baseline: " + baselineTime + "ms");
            System.out.println("  Change: " + String.format("%.2f", degradation) + "%");

            if (degradation > 20.0) { // More than 20% slower
                System.out.println("  ❌ PERFORMANCE REGRESSION DETECTED!");
                hasRegression = true;
            } else {
                System.out.println("  ✅ Performance within acceptable range");
            }
        }

        // Update baseline with current metrics
        saveBaselineMetrics(currentMetrics);

        // Fail test if performance regression detected (but allow first run to establish baseline)
        if (hasRegression && !baselineMetrics.isEmpty()) {
            throw new AssertionError("Performance regression detected! Check logs above.");
        }
    }

    private long measureEndpointPerformance(String endpoint, UserRequestDto request) throws Exception {
        long startTime = System.nanoTime();

        int expectedStatus = endpoint.contains("register") ? 201 : 200;

        mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is(expectedStatus));

        long endTime = System.nanoTime();
        return java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }

    private UserRequestDto createTestUser(String prefix) {
        return UserRequestDto.builder()
            .username(prefix + System.currentTimeMillis())
            .password("VeryStrongPass123!")
            .build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Long> loadBaselineMetrics() {
        try {
            Path path = Paths.get("target", "test-classes", PERFORMANCE_BASELINE_FILE);
            if (Files.exists(path)) {
                String content = Files.readString(path);
                Map<String, Object> rawMap = objectMapper.readValue(content, Map.class);
                Map<String, Long> result = new HashMap<>();
                for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                    if (entry.getValue() instanceof Number) {
                        result.put(entry.getKey(), ((Number) entry.getValue()).longValue());
                    }
                }
                return result;
            }
        } catch (Exception e) {
            System.out.println("Could not load performance baseline: " + e.getMessage());
        }
        return new HashMap<>();
    }

    private void saveBaselineMetrics(Map<String, Long> metrics) {
        try {
            Path targetDir = Paths.get("target", "test-classes");
            Files.createDirectories(targetDir);
            Path path = targetDir.resolve(PERFORMANCE_BASELINE_FILE);
            String content = objectMapper.writeValueAsString(metrics);
            Files.writeString(path, content);
            System.out.println("Performance baseline saved to: " + path);
        } catch (Exception e) {
            System.err.println("Could not save performance baseline: " + e.getMessage());
        }
    }
}
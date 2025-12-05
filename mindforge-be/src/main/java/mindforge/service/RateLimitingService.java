package mindforge.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class RateLimitingService {

    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    private static class RateLimitInfo {
        private final AtomicInteger attempts;
        private final LocalDateTime windowStart;
        private final LocalDateTime windowEnd;

        public RateLimitInfo(int maxAttempts, int windowMinutes) {
            this.attempts = new AtomicInteger(0);
            this.windowStart = LocalDateTime.now();
            this.windowEnd = windowStart.plusMinutes(windowMinutes);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(windowEnd);
        }

        public boolean tryIncrement(int maxAttempts) {
            int current = attempts.incrementAndGet();
            return current <= maxAttempts;
        }

        public int getAttempts() {
            return attempts.get();
        }
    }

    public boolean isAllowed(String key, int maxAttempts, int windowMinutes) {
        RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo(maxAttempts, windowMinutes));

        // Reset if window has expired
        if (info.isExpired()) {
            rateLimitMap.remove(key);
            info = new RateLimitInfo(maxAttempts, windowMinutes);
            rateLimitMap.put(key, info);
        }

        return info.tryIncrement(maxAttempts);
    }

    public int getRemainingAttempts(String key, int maxAttempts) {
        RateLimitInfo info = rateLimitMap.get(key);
        if (info == null || info.isExpired()) {
            return maxAttempts;
        }
        return Math.max(0, maxAttempts - info.getAttempts());
    }

    public void clearRateLimit(String key) {
        rateLimitMap.remove(key);
    }
}
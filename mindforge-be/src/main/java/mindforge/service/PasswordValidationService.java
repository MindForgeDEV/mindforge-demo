package mindforge.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class PasswordValidationService {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;

    // Password strength levels
    public enum PasswordStrength {
        WEAK, FAIR, GOOD, STRONG
    }

    public static class PasswordValidationResult {
        private final boolean valid;
        private final PasswordStrength strength;
        private final String message;

        public PasswordValidationResult(boolean valid, PasswordStrength strength, String message) {
            this.valid = valid;
            this.strength = strength;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public PasswordStrength getStrength() { return strength; }
        public String getMessage() { return message; }
    }

    public PasswordValidationResult validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return new PasswordValidationResult(false, PasswordStrength.WEAK, "Password cannot be empty");
        }

        String trimmedPassword = password.trim();

        // Check minimum length
        if (trimmedPassword.length() < MIN_LENGTH) {
            return new PasswordValidationResult(false, PasswordStrength.WEAK,
                "Password must be at least " + MIN_LENGTH + " characters long");
        }

        // Check maximum length
        if (trimmedPassword.length() > MAX_LENGTH) {
            return new PasswordValidationResult(false, PasswordStrength.WEAK,
                "Password must be less than " + MAX_LENGTH + " characters long");
        }

        // Calculate strength
        PasswordStrength strength = calculateStrength(trimmedPassword);

        // For now, we only require FAIR strength or better
        boolean isValid = strength != PasswordStrength.WEAK;

        String message = getStrengthMessage(strength);

        return new PasswordValidationResult(isValid, strength, message);
    }

    private PasswordStrength calculateStrength(String password) {
        int score = 0;

        // Length bonus
        if (password.length() >= 12) score += 2;
        else if (password.length() >= 8) score += 1;

        // Character variety bonuses
        if (Pattern.compile("[a-z]").matcher(password).find()) score += 1; // lowercase
        if (Pattern.compile("[A-Z]").matcher(password).find()) score += 1; // uppercase
        if (Pattern.compile("[0-9]").matcher(password).find()) score += 1; // digits
        if (Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) score += 1; // special chars

        // Common patterns penalty
        if (Pattern.compile("(.)\1{2,}").matcher(password).find()) score -= 1; // repeated chars
        if (Pattern.compile("123|abc|qwe|password|admin", Pattern.CASE_INSENSITIVE).matcher(password).find()) score -= 1;

        if (score >= 5) return PasswordStrength.STRONG;
        if (score >= 3) return PasswordStrength.GOOD;
        if (score >= 2) return PasswordStrength.FAIR;
        return PasswordStrength.WEAK;
    }

    private String getStrengthMessage(PasswordStrength strength) {
        switch (strength) {
            case STRONG: return "Very strong password";
            case GOOD: return "Strong password";
            case FAIR: return "Fair password - consider adding more variety";
            case WEAK: return "Weak password - must include uppercase, lowercase, and numbers";
            default: return "Password strength unknown";
        }
    }
}
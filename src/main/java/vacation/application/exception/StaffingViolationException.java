package vacation.application.exception;

import java.time.LocalDate;
import java.util.Map;

public class StaffingViolationException extends RuntimeException {

    private final Map<LocalDate, Integer> violations;

    public StaffingViolationException(Map<LocalDate, Integer> violations) {
        super("Staffing constraint violated");
        this.violations = violations;
    }

    public Map<LocalDate, Integer> getViolations() {
        return violations;
    }
}

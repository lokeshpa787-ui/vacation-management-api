package vacation.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class RemainingDaysResponse {
    private UUID employeeId;
    private int year;
    private int totalAllowed;
    private int takenApproved;
    private int remaining;
}


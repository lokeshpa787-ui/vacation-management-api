package vacation.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record OverlapRequestDto(
        UUID requestId,
        UUID employeeId,
        LocalDate vacationStartDate,
        LocalDate vacationEndDate
) {}

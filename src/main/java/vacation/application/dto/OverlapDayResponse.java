package vacation.application.dto;

import java.time.LocalDate;
import java.util.List;

public record OverlapDayResponse(
        LocalDate date,
        int count,
        List<OverlapRequestDto> requestsOnDate
) {}

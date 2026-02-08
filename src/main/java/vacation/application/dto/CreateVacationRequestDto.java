package vacation.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class CreateVacationRequestDto {

    @NotNull
    private LocalDate vacationStartDate;

    @NotNull
    private LocalDate vacationEndDate;

    private String comment;
}


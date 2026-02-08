package vacation.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DecisionDto {
    @NotBlank
    private String action; // approve | reject
}


package vacation.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TokenRequestDto {

    @NotNull
    private UUID employeeId;

    @NotBlank
    private String role; // ROLE_WORKER or ROLE_MANAGER
}

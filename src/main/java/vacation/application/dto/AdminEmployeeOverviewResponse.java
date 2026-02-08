package vacation.application.dto;

import lombok.Builder;
import lombok.Getter;
import vacation.application.entity.VacationRequest;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AdminEmployeeOverviewResponse {

    private UUID employeeId;
    private String name;

    private int totalAllowed;
    private int takenApproved;

    private List<VacationRequest> pendingRequests;
    private List<VacationRequest> approvedRequests;
}

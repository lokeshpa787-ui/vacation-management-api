package vacation.application.controller.admin;

import vacation.application.dto.AdminEmployeeOverviewResponse;
import vacation.application.dto.DecisionDto;
import vacation.application.entity.VacationRequest;
import vacation.application.entity.VacationStatus;
import vacation.application.repository.VacationRequestRepository;
import vacation.application.service.ManagerVacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import vacation.application.dto.OverlapDayResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminVacationController {

    private final ManagerVacationService service;
    private final VacationRequestRepository repo;

    @GetMapping("/requests")
    public List<VacationRequest> all(
            @RequestParam Optional<String> status) {

        return status
                .map(s -> repo.findByStatus(
                        VacationStatus.valueOf(s.toUpperCase())))
                .orElse(repo.findAll());
    }

    @PostMapping("/requests/{id}/decision")
    public VacationRequest decide(
            JwtAuthenticationToken auth,
            @PathVariable UUID id,
            @RequestBody DecisionDto dto) {

        return service.decide(
                UUID.fromString(auth.getName()),
                id,
                dto.getAction());
    }

    @GetMapping("/overlaps")
    public List<OverlapDayResponse> overlaps(@RequestParam int year) {
        return service.findOverlaps(year);
    }


    @GetMapping("/employees/{employeeId}/overview")
    public AdminEmployeeOverviewResponse employeeOverview(
            @PathVariable UUID employeeId,
            @RequestParam int year) {

        return service.employeeOverview(employeeId, year);
    }
}

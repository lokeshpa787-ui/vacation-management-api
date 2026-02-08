package vacation.application.controller.worker;

import vacation.application.dto.CreateVacationRequestDto;
import vacation.application.dto.RemainingDaysResponse;
import vacation.application.entity.VacationRequest;
import vacation.application.entity.VacationStatus;
import vacation.application.repository.VacationRequestRepository;
import vacation.application.service.WorkerVacationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/worker")
@RequiredArgsConstructor
public class WorkerVacationController {

    private final WorkerVacationService service;
    private final VacationRequestRepository repo;

    @GetMapping("/requests")
    public List<VacationRequest> list(
            JwtAuthenticationToken auth,
            @RequestParam Optional<String> status) {

        UUID userId = UUID.fromString(auth.getName());

        return status
                .map(s -> repo.findByAuthorIdAndStatus(
                        userId,
                        VacationStatus.valueOf(s.toUpperCase())))
                .orElse(repo.findByAuthorId(userId));
    }

    @PostMapping("/requests")
    public VacationRequest create(
            JwtAuthenticationToken auth,
            @Valid @RequestBody CreateVacationRequestDto dto) {

        return service.create(UUID.fromString(auth.getName()), dto);
    }

    @GetMapping("/remaining-days")
    public RemainingDaysResponse remaining(JwtAuthenticationToken auth) {
        return service.remainingDays(
                UUID.fromString(auth.getName()),
                LocalDate.now().getYear());
    }
    @GetMapping("/requests/{id}")
    public VacationRequest getById(
            JwtAuthenticationToken auth,
            @PathVariable UUID id) {

        UUID userId = UUID.fromString(auth.getName());

        return repo.findByIdAndAuthorId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vacation request not found"
                ));
    }

}

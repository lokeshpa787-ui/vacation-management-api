package vacation.application.service;

import vacation.application.dto.CreateVacationRequestDto;
import vacation.application.dto.RemainingDaysResponse;
import vacation.application.entity.Employee;
import vacation.application.entity.VacationRequest;
import vacation.application.entity.VacationStatus;
import vacation.application.repository.EmployeeRepository;
import vacation.application.repository.VacationRequestRepository;
import vacation.application.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerVacationService {

    private final VacationRequestRepository vacationRepo;
    private final EmployeeRepository employeeRepo;

    public RemainingDaysResponse remainingDays(UUID employeeId, int year) {

        Employee emp = employeeRepo.findById(employeeId).orElseThrow();

        int approvedDays =
                vacationRepo.findByAuthorIdAndStatus(employeeId, VacationStatus.APPROVED)
                        .stream()
                        .filter(v -> v.getVacationStartDate().getYear() == year)
                        .mapToInt(v -> DateUtil.daysInclusive(
                                v.getVacationStartDate(),
                                v.getVacationEndDate()))
                        .sum();

        return RemainingDaysResponse.builder()
                .employeeId(employeeId)
                .year(year)
                .totalAllowed(emp.getTotalVacationDaysPerYear())
                .takenApproved(approvedDays)
                .remaining(emp.getTotalVacationDaysPerYear() - approvedDays)
                .build();
    }


    public VacationRequest create(UUID employeeId, CreateVacationRequestDto dto) {

        if (dto.getVacationStartDate().getYear() != dto.getVacationEndDate().getYear()) {
            throw new IllegalArgumentException(
                    "Vacation requests spanning multiple calendar years are not supported"
            );
        }

        if (dto.getVacationStartDate().isAfter(dto.getVacationEndDate())) {
            throw new IllegalArgumentException("startDate must be <= endDate");
        }

        RemainingDaysResponse remaining =
                remainingDays(employeeId, dto.getVacationStartDate().getYear());

        int requested =
                DateUtil.daysInclusive(dto.getVacationStartDate(), dto.getVacationEndDate());

        if (requested > remaining.getRemaining()) {
            throw new IllegalStateException(
                    "Creating this request would exceed your approved vacation allowance for the year"
            );
        }

        VacationRequest vr = VacationRequest.builder()
                .id(UUID.randomUUID())
                .authorId(employeeId)
                .status(VacationStatus.PENDING)
                .requestCreatedAt(Instant.now())
                .vacationStartDate(dto.getVacationStartDate())
                .vacationEndDate(dto.getVacationEndDate())
                .comment(dto.getComment())
                .build();

        return vacationRepo.save(vr);
    }
}

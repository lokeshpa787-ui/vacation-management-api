package vacation.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vacation.application.dto.CreateVacationRequestDto;
import vacation.application.entity.Employee;
import vacation.application.entity.VacationRequest;
import vacation.application.entity.VacationStatus;
import vacation.application.repository.EmployeeRepository;
import vacation.application.repository.VacationRequestRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkerVacationServiceTest {

    @InjectMocks
    private WorkerVacationService service;

    @Mock
    private VacationRequestRepository repo;

    @Mock
    private EmployeeRepository employeeRepo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // 1️⃣ Remaining-days calculation
    @Test
    void remainingDays_sumsApprovedDaysCorrectly() {
        UUID userId = UUID.randomUUID();
        int year = 2026;

        VacationRequest r1 = VacationRequest.builder()
                .status(VacationStatus.APPROVED)
                .vacationStartDate(LocalDate.of(2026, 1, 1))
                .vacationEndDate(LocalDate.of(2026, 1, 5)) // 5 days
                .build();

        VacationRequest r2 = VacationRequest.builder()
                .status(VacationStatus.APPROVED)
                .vacationStartDate(LocalDate.of(2026, 3, 1))
                .vacationEndDate(LocalDate.of(2026, 3, 3)) // 3 days
                .build();

        // ✅ correct repository method
        when(repo.findByAuthorIdAndStatus(userId, VacationStatus.APPROVED))
                .thenReturn(List.of(r1, r2));

        when(employeeRepo.findById(userId))
                .thenReturn(Optional.of(
                        Employee.builder()
                                .id(userId)
                                .totalVacationDaysPerYear(30)
                                .build()
                ));

        int remaining = service.remainingDays(userId, year).getRemaining();

        assertEquals(22, remaining);
    }

    // 2️⃣ Cannot exceed yearly allowance
    @Test
    void createRequest_failsIfExceedsAllowance() {
        UUID userId = UUID.randomUUID();

        VacationRequest existing = VacationRequest.builder()
                .status(VacationStatus.APPROVED)
                .vacationStartDate(LocalDate.of(2026, 1, 1))
                .vacationEndDate(LocalDate.of(2026, 1, 25)) // 25 days
                .build();

        when(repo.findByAuthorIdAndStatus(userId, VacationStatus.APPROVED))
                .thenReturn(List.of(existing));

        when(employeeRepo.findById(userId))
                .thenReturn(Optional.of(
                        Employee.builder()
                                .id(userId)
                                .totalVacationDaysPerYear(30)
                                .build()
                ));

        CreateVacationRequestDto dto = new CreateVacationRequestDto();
        dto.setVacationStartDate(LocalDate.of(2026, 2, 1));
        dto.setVacationEndDate(LocalDate.of(2026, 2, 10));

        assertThrows(IllegalStateException.class,
                () -> service.create(userId, dto));
    }

}

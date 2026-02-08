//package vacation.application.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import vacation.application.config.AppProperties;
//import vacation.application.entity.VacationRequest;
//import vacation.application.entity.VacationStatus;
//import vacation.application.repository.VacationRequestRepository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ManagerVacationServiceTest {
//
//    @InjectMocks
//    private ManagerVacationService service;
//
//    @Mock
//    private VacationRequestRepository repo;
//
//    @Mock
//    private AppProperties properties;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//        when(properties.getStaffing().getMinEmployeesOnSite()).thenReturn(2);
//    }
//
//    // 4️⃣ Approval allowed
//    @Test
//    void approveAllowed_whenMinEmployeesSatisfied() {
//        VacationRequest pending = VacationRequest.builder()
//                .id(UUID.randomUUID())
//                .status(VacationStatus.PENDING)
//                .vacationStartDate(LocalDate.of(2026, 7, 1))
//                .vacationEndDate(LocalDate.of(2026, 7, 5))
//                .build();
//
//        when(repo.findApprovedOverlapping(any(), any()))
//                .thenReturn(List.of()); // no overlaps
//
//        when(repo.findById(any())).thenReturn(java.util.Optional.of(pending));
//
//        VacationRequest result = service.decide(
//                UUID.randomUUID(),
//                pending.getId(),
//                "approve"
//        );
//
//        assertEquals(VacationStatus.APPROVED, result.getStatus());
//    }
//
//    // 4️⃣ Approval rejected
//    @Test
//    void approveRejected_whenMinEmployeesViolated() {
//        VacationRequest pending = VacationRequest.builder()
//                .id(UUID.randomUUID())
//                .status(VacationStatus.PENDING)
//                .vacationStartDate(LocalDate.of(2026, 7, 1))
//                .vacationEndDate(LocalDate.of(2026, 7, 5))
//                .build();
//
//        when(repo.findApprovedOverlapping(any(), any()))
//                .thenReturn(List.of(
//                        VacationRequest.builder().status(VacationStatus.APPROVED).build(),
//                        VacationRequest.builder().status(VacationStatus.APPROVED).build()
//                ));
//
//        when(repo.findById(any())).thenReturn(java.util.Optional.of(pending));
//
//        assertThrows(IllegalStateException.class,
//                () -> service.decide(UUID.randomUUID(), pending.getId(), "approve"));
//    }
//}

package vacation.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vacation.application.config.AppProperties;
import vacation.application.entity.Employee;
import vacation.application.entity.VacationRequest;
import vacation.application.entity.VacationStatus;
import vacation.application.exception.StaffingViolationException;
import vacation.application.repository.EmployeeRepository;
import vacation.application.repository.VacationRequestRepository;
import vacation.application.dto.OverlapDayResponse;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManagerVacationServiceTest {

    @InjectMocks
    private ManagerVacationService service;

    @Mock
    private VacationRequestRepository repo;

    @Mock
    private EmployeeRepository employeeRepo;

    @Mock
    private AppProperties properties;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // ✅ Mock staffing config
        AppProperties.Staffing staffing = new AppProperties.Staffing();
        staffing.setMinEmployeesOnSite(2);
        when(properties.getStaffing()).thenReturn(staffing);

        // ✅ 3 employees total
        when(employeeRepo.findAll()).thenReturn(List.of(
                Employee.builder().build(),
                Employee.builder().build(),
                Employee.builder().build()
        ));
    }

    // ✅ Approval allowed
    @Test
    void approveAllowed_whenMinEmployeesSatisfied() {
        VacationRequest pending = VacationRequest.builder()
                .id(UUID.randomUUID())
                .status(VacationStatus.PENDING)
                .vacationStartDate(LocalDate.of(2026, 7, 1))
                .vacationEndDate(LocalDate.of(2026, 7, 5))
                .build();

        when(repo.findById(any())).thenReturn(Optional.of(pending));
        when(repo.findApprovedOverlapping(any(), any()))
                .thenReturn(List.of());

        // ✅ IMPORTANT: mock save()
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        VacationRequest result =
                service.decide(UUID.randomUUID(), pending.getId(), "approve");

        assertEquals(VacationStatus.APPROVED, result.getStatus());
    }

    // ✅ Approval rejected
    @Test
    void approveRejected_whenMinEmployeesViolated() {
        VacationRequest pending = VacationRequest.builder()
                .id(UUID.randomUUID())
                .status(VacationStatus.PENDING)
                .vacationStartDate(LocalDate.of(2026, 7, 1))
                .vacationEndDate(LocalDate.of(2026, 7, 5))
                .build();

        VacationRequest approved1 = VacationRequest.builder()
                .status(VacationStatus.APPROVED)
                .vacationStartDate(LocalDate.of(2026, 7, 1))
                .vacationEndDate(LocalDate.of(2026, 7, 5))
                .build();

        VacationRequest approved2 = VacationRequest.builder()
                .status(VacationStatus.APPROVED)
                .vacationStartDate(LocalDate.of(2026, 7, 1))
                .vacationEndDate(LocalDate.of(2026, 7, 5))
                .build();

        when(repo.findById(any())).thenReturn(Optional.of(pending));
        when(repo.findApprovedOverlapping(any(), any()))
                .thenReturn(List.of(approved1, approved2));

        assertThrows(StaffingViolationException.class,
                () -> service.decide(UUID.randomUUID(), pending.getId(), "approve"));

    }

    @Test
    void detectsOverlappingDays() {

        VacationRequest a = VacationRequest.builder()
                .id(UUID.randomUUID())
                .authorId(UUID.randomUUID())
                .vacationStartDate(LocalDate.of(2026, 12, 20))
                .vacationEndDate(LocalDate.of(2026, 12, 22))
                .status(VacationStatus.APPROVED)
                .build();

        VacationRequest b = VacationRequest.builder()
                .id(UUID.randomUUID())
                .authorId(UUID.randomUUID())
                .vacationStartDate(LocalDate.of(2026, 12, 22))
                .vacationEndDate(LocalDate.of(2026, 12, 24))
                .status(VacationStatus.PENDING)
                .build();

        when(repo.findByStatusIn(any()))
                .thenReturn(List.of(a, b));

        List<OverlapDayResponse> result =
                service.findOverlaps(2026);

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2026, 12, 22), result.get(0).date());
        assertEquals(2, result.get(0).count());
    }

}


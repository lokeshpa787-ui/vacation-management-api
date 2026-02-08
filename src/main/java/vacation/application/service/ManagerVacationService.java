package vacation.application.service;

import vacation.application.config.AppProperties;
import vacation.application.dto.OverlapDayResponse;
import vacation.application.dto.OverlapRequestDto;
import vacation.application.entity.Employee;
import vacation.application.entity.VacationRequest;
import vacation.application.entity.VacationStatus;
import vacation.application.exception.StaffingViolationException;
import vacation.application.repository.EmployeeRepository;
import vacation.application.repository.VacationRequestRepository;
import vacation.application.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vacation.application.dto.AdminEmployeeOverviewResponse;


import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerVacationService {

    private final VacationRequestRepository vacationRepo;
    private final EmployeeRepository employeeRepo;
    private final AppProperties properties;

    public VacationRequest decide(UUID managerId, UUID requestId, String action) {

        VacationRequest req = vacationRepo.findById(requestId).orElseThrow();

        // ✅ ADD THIS BLOCK RIGHT HERE
        if (req.getStatus() != VacationStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING vacation requests can be approved or rejected"
            );
        }

        if ("reject".equalsIgnoreCase(action)) {
            req.setStatus(VacationStatus.REJECTED);
            req.setResolvedBy(managerId);
            req.setResolvedAt(Instant.now());
            return vacationRepo.save(req);
        }

        List<Employee> allEmployees = employeeRepo.findAll();
        int maxAbsent =
                allEmployees.size() - properties.getStaffing().getMinEmployeesOnSite();

        Map<LocalDate, Long> absentCount = new HashMap<>();

        List<VacationRequest> approved =
                vacationRepo.findApprovedOverlapping(
                        req.getVacationStartDate(),
                        req.getVacationEndDate());

        for (VacationRequest vr : approved) {
            DateUtil.expand(vr.getVacationStartDate(), vr.getVacationEndDate())
                    .forEach(d -> absentCount.merge(d, 1L, Long::sum));
        }

        Map<LocalDate, Integer> violations = new HashMap<>();

        DateUtil.expand(req.getVacationStartDate(), req.getVacationEndDate())
                .forEach(d -> {
                    int wouldBeAbsent =
                            Math.toIntExact(absentCount.getOrDefault(d, 0L)) + 1;

                    if (wouldBeAbsent > maxAbsent) {
                        violations.put(d, wouldBeAbsent);
                    }
                });

        if (!violations.isEmpty()) {
            throw new StaffingViolationException(violations);
        }

        req.setStatus(VacationStatus.APPROVED);
        req.setResolvedBy(managerId);
        req.setResolvedAt(Instant.now());

        return vacationRepo.save(req);
    }

    // ✅ NEW: overlaps API logic
    public List<OverlapDayResponse> findOverlaps(int year) {

        List<VacationRequest> requests =
                vacationRepo.findByStatusIn(
                        List.of(VacationStatus.PENDING, VacationStatus.APPROVED)
                );

        Map<LocalDate, List<VacationRequest>> byDate = new HashMap<>();

        for (VacationRequest vr : requests) {
            DateUtil.expand(vr.getVacationStartDate(), vr.getVacationEndDate())
                    .stream()
                    .filter(d -> d.getYear() == year)
                    .forEach(d ->
                            byDate.computeIfAbsent(d, k -> new ArrayList<>())
                                    .add(vr)
                    );

        }

        return byDate.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> new OverlapDayResponse(
                        e.getKey(),
                        e.getValue().size(),
                        e.getValue().stream()
                                .map(v -> new OverlapRequestDto(
                                        v.getId(),
                                        v.getAuthorId(),
                                        v.getVacationStartDate(),
                                        v.getVacationEndDate()
                                ))
                                .toList()
                ))
                .sorted(Comparator.comparing(OverlapDayResponse::date))
                .toList();
    }

    public AdminEmployeeOverviewResponse employeeOverview(UUID employeeId, int year) {

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

//        List<VacationRequest> approved =
//                vacationRepo.findByAuthorIdAndStatus(employeeId, VacationStatus.APPROVED);
        List<VacationRequest> approved =
                vacationRepo.findByAuthorIdAndStatus(employeeId, VacationStatus.APPROVED)
                        .stream()
                        .filter(v -> v.getVacationStartDate().getYear() == year)
                        .toList();

        List<VacationRequest> pending =
                vacationRepo.findByAuthorIdAndStatus(employeeId, VacationStatus.PENDING);

        int takenApproved =
                approved.stream()
                        .filter(v -> v.getVacationStartDate().getYear() == year)
                        .mapToInt(v -> DateUtil.daysInclusive(
                                v.getVacationStartDate(),
                                v.getVacationEndDate()))
                        .sum();

        return AdminEmployeeOverviewResponse.builder()
                .employeeId(employeeId)
                .name(employee.getName())
                .totalAllowed(employee.getTotalVacationDaysPerYear())
                .takenApproved(takenApproved)
                .approvedRequests(approved)
                .pendingRequests(pending)
                .build();
    }

}

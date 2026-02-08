package vacation.application.repository;

import vacation.application.entity.VacationRequest;
import vacation.application.entity.VacationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VacationRequestRepository
        extends JpaRepository<VacationRequest, UUID> {



    List<VacationRequest> findByStatusIn(List<VacationStatus> statuses);


    List<VacationRequest> findByAuthorId(UUID authorId);

    List<VacationRequest> findByStatus(VacationStatus status);

    List<VacationRequest> findByAuthorIdAndStatus(
            UUID authorId,
            VacationStatus status
    );

    // ✅ ADD THIS
    Optional<VacationRequest> findByIdAndAuthorId(
            UUID id,
            UUID authorId
    );

    @Query("""
        select vr from VacationRequest vr
        where vr.status = 'APPROVED'
          and vr.vacationStartDate <= :end
          and vr.vacationEndDate >= :start
    """)
    List<VacationRequest> findApprovedOverlapping(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}

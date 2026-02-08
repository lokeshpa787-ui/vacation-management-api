package vacation.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vacation_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationRequest {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID authorId;

    @Enumerated(EnumType.STRING)
    private VacationStatus status;

    private UUID resolvedBy;

    private Instant requestCreatedAt;

    private Instant resolvedAt;

    private LocalDate vacationStartDate;

    private LocalDate vacationEndDate;

    private String comment;
}


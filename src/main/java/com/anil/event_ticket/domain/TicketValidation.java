package com.anil.event_ticket.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ticket_validation",indexes = {@Index(name = "idx_ticket_validation_ticket_id", columnList = "ticket_id"),@Index(name = "idx_ticket_validation_staff_id", columnList = "validated_by")})
@NoArgsConstructor(access = AccessLevel.PROTECTED,force = true)
@Getter
public class TicketValidation{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private final TicketValidationStatusEnum status;

    @Column(name = "validation_method",nullable = false)
    @Enumerated(EnumType.STRING)
    private final TicketValidationMethodEnum validationMethod;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "ticket_id",nullable = false,updatable = false)
    private final Ticket ticket;

    @Column(name = "validated_at", nullable = false, updatable = false)
    private final Instant validatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "validated_by")
    private final User validatedBy; // CRITICAL for the "Staff" role audit

    @Builder
    public TicketValidation(
            @NonNull Ticket ticket,
            @NonNull User validatedBy,
            @NonNull TicketValidationStatusEnum status,
            @NonNull TicketValidationMethodEnum validationMethod
    ) {
        this.ticket = ticket;
        this.validatedBy = validatedBy;
        this.status = status;
        this.validationMethod = validationMethod;
        this.validatedAt = Instant.now();
    }
}

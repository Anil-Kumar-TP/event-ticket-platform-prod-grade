package com.anil.event_ticket.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",updatable = false,nullable = false)
    private UUID id;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "ticket_type_id",nullable = false,updatable = false)
    private TicketType ticketType;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "purchaser_id",nullable = false)
    private User purchaser;

    void setTicketType(@NonNull TicketType ticketType) {
        this.ticketType = ticketType;
    }

    void setPurchaser(@NonNull User purchaser) {
        this.purchaser = purchaser;
    }

    @CreatedDate
    @Column(name = "created_at",updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id);  // Null-safe
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;  // Consistent
    }
}

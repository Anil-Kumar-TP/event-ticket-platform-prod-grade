package com.anil.event_ticket.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tickets",indexes = {@Index(columnList = "ticket_type_id"),@Index(columnList = "purchaser_id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Ticket extends BaseEntity{

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private TicketStatusEnum status = TicketStatusEnum.PURCHASED;


    public void markAsUsed() {
        if (this.status != TicketStatusEnum.PURCHASED) {
            throw new IllegalStateException("Only PURCHASED tickets can be marked as USED. Current status: " + this.status);
        }
        this.status = TicketStatusEnum.USED;
    }

    /**
     * Transitions ticket to CANCELLED.
     */
    public void cancel() {
        if (this.status == TicketStatusEnum.USED) {
            throw new IllegalStateException("Cannot cancel a ticket that has already been used.");
        }
        this.status = TicketStatusEnum.CANCELLED;
    }

    public boolean isValidForEntry() {
        return status == TicketStatusEnum.PURCHASED;
    }

    @Version
    private Long version;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "ticket_type_id",nullable = false,updatable = false)
    private TicketType ticketType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "purchaser_id",nullable = false)
    private User purchaser;

    void setTicketType(@NonNull TicketType ticketType) {
        this.ticketType = ticketType;
    }

    void setPurchaser(@NonNull User purchaser) {
        this.purchaser = purchaser;
    }
}

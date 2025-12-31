package com.anil.event_ticket.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ticket_types",indexes = @Index(columnList = "event_id"),uniqueConstraints = @UniqueConstraint(columnNames = {"event_id","name"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class TicketType extends BaseEntity{

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "price",nullable = false)
    private BigDecimal price;

    @Positive
    @Column(name = "total_quantity", nullable = false,updatable = false)
    private int totalQuantity;

    @PositiveOrZero
    @Column(name = "remaining_quantity", nullable = false)
    private int remainingQuantity;

    /**
     * Decrements inventory. This is the ONLY way to reduce stock.
     * Enforces the invariant: remainingQuantity >= 0
     */
    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Reservation quantity must be positive");
        }
        if (this.remainingQuantity < quantity) {
            // This is a Domain Exception. It tells the Service "I can't do this."
            throw new IllegalStateException("Insufficient inventory for TicketType: " + this.name);
        }
        this.remainingQuantity -= quantity;
    }

    /**
     * Increments inventory (e.g., if a purchase is cancelled).
     */
    public void release(int quantity) {
        if (this.remainingQuantity + quantity > this.totalQuantity) {
            throw new IllegalStateException("Cannot release more than total capacity");
        }
        this.remainingQuantity += quantity;
    }

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "event_id",nullable = false,updatable = false)
    private Event event;

    // Helper — only way to change event
    void setEvent(Event event) {
        this.event = Objects.requireNonNull(event);
    }
}

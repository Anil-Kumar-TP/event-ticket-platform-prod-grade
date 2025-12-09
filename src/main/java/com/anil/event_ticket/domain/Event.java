package com.anil.event_ticket.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",updatable = false,nullable = false)
    private UUID id;

    @EqualsAndHashCode.Include
    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "start")
    private LocalDateTime start;

    @Column(name = "end")
    private LocalDateTime end;

    @EqualsAndHashCode.Include
    @Column(name = "venue",nullable = false)
    private String venue;

    @Column(name = "sales_start")
    private LocalDateTime salesStart;

    @Column(name = "sales_end")
    private LocalDateTime salesEnd;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "organizer_id",nullable = false)
    private User organizer;

//    For attendees/staff → User is the owning side → the collection is the source of truth → adding to the collection is the ONLY way to change the relationship.
//    For organizer → Event is the owning side → the foreign key column (organizer_id) is the source of truth → changing the collection on User does NOTHING.
    //this is why the helper method looks different.
    public void setOrganizer(User organizer) {
        this.organizer = Objects.requireNonNull(organizer);
    }

    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private Set<TicketType> ticketTypes = new HashSet<>();

    public void addTicketType(TicketType ticketType) {
        ticketTypes.add(Objects.requireNonNull(ticketType));
        ticketType.setEvent(this);  // ← maintain both sides
    }

    public void removeTicketType(TicketType ticketType) {
        ticketTypes.remove(ticketType);
        ticketType.setEvent(null);
    }

    @CreatedDate
    @Column(name = "created_at",updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;
}

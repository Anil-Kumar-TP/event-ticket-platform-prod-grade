package com.anil.event_ticket.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Table(name = "events",indexes = {@Index(columnList = "start_time"),@Index(columnList = "status"),@Index(columnList = "sales_end")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Event extends BaseEntity{

    @NotBlank
    @Column(name = "name",nullable = false)
    private String name;

    @FutureOrPresent
    @Column(name = "start_time")
    private ZonedDateTime start;

    @Column(name = "end_time")
    private ZonedDateTime end;

    @NotBlank
    @Column(name = "venue",nullable = false)
    private String venue;

    @Column(name = "sales_start")
    private ZonedDateTime salesStart;

    @Column(name = "sales_end")
    private ZonedDateTime salesEnd;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatusEnum status;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "organizer_id",nullable = false)
    private User organizer;

    @ManyToMany
    @JoinTable(
            name = "event_staff",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"})
    )
    @Builder.Default
    private Set<User> staff = new HashSet<>();

    public void assignStaff(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        staff.add(user);
        user.getStaffingEvents().add(this);
    }

    public void removeStaff(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        staff.remove(user);
        user.getStaffingEvents().remove(this);
    }


    // Business Rule - Is it currently possible to buy a ticket?
    public boolean canAcceptPurchases() {
        ZonedDateTime now = ZonedDateTime.now();
        return this.status == EventStatusEnum.PUBLISHED
                && now.isAfter(salesStart)
                && now.isBefore(salesEnd);
    }

    // Business Rule - Is it too late to validate a ticket?
    public boolean isPastEventEnd() {
        return ZonedDateTime.now().isAfter(end);
    }


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

    // Standard getters/setters omitted by Lombok @Getter.
    // We only manually add setters for things that are editable.
    public void updateDetails(String name, String venue) {
        this.name = name;
        this.venue = venue;
    }
}

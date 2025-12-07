package com.anil.event_ticket.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",updatable = false,nullable = false)
    private UUID id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @Column(name = "password",nullable = false)
    private String password;

    //all events user organizes
    @OneToMany(mappedBy = "organizer",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private Set<Event> organizedEvents = new HashSet<>();
    //all events user attends

    @ManyToMany
    @JoinTable(name = "event_attendees",joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "event_id"),uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
    @Builder.Default
    private Set<Event> attendingEvents = new HashSet<>();
    //all events user staffs

    @ManyToMany
    @JoinTable(name = "event_staff",joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "event_id"),uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
    @Builder.Default
    private Set<Event> staffingEvents = new HashSet<>();

    public void attendEvent(Event event) {
        attendingEvents.add(event);
    }

    public void staffEvent(Event event) {
        staffingEvents.add(event);
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles",joinColumns = @JoinColumn(name = "user_id"),uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","role"}))
    @Column(name = "role")
    @Builder.Default
    private Set<RolesEnum> roles = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at",updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;
}

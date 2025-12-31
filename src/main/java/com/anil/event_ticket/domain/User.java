package com.anil.event_ticket.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.*;

@Entity
@Table(name = "users",indexes = @Index(columnList = "email"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity{

    @NotBlank
    @Column(name = "name",nullable = false)
    private String name;

    @Email
    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @NotBlank
    @Column(name = "password",nullable = false)
    private String password;

    @Version
    private Long version;

    @OneToMany(mappedBy = "organizer",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private Set<Event> organizedEvents = new HashSet<>();

    @ManyToMany(mappedBy = "staff")
    @Builder.Default
    private Set<Event> staffingEvents = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles",joinColumns = @JoinColumn(name = "user_id"),uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","role"}))
    @Column(name = "role")
    @Builder.Default
    private Set<RolesEnum> roles = new HashSet<>();

}

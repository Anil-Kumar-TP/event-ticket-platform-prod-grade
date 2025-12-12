package com.anil.event_ticket.auth.entity;

import com.anil.event_ticket.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens",indexes = {@Index(name = "idx_refresh_token",columnList = "token",unique = true)})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",updatable = false,nullable = false)
    private UUID id;

    @Column(nullable = false,unique = true,length = 512)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false,updatable = false)
    private User user;

    public void issuedTo(@NonNull User user) {
        this.user = Objects.requireNonNull(user, "Refresh token must be linked to a user");
    }

    public static RefreshToken createFor(@NonNull User user, String token, Instant expiry){
        RefreshToken rt = new RefreshToken();
        rt.issuedTo(user);
        rt.token = token;
        rt.expiryDate = expiry;
        return rt;
    }

    @CreatedDate
    @Column(name = "created_at",updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;
}

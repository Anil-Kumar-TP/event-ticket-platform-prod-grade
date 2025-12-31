package com.anil.event_ticket.auth.entity;

import com.anil.event_ticket.domain.BaseEntity;
import com.anil.event_ticket.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "refresh_tokens",indexes = {@Index(name = "idx_refresh_token",columnList = "token",unique = true),@Index(name = "idx_refresh_user",columnList = "user_id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(AccessLevel.NONE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken extends BaseEntity {

    @Column(nullable = false,unique = true,length = 512)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

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
}

package com.anil.event_ticket.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "qr_codes",indexes = {@Index(columnList = "value"),@Index(columnList = "ticket_id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class QrCode extends BaseEntity{

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private QrCodeStatusEnum status = QrCodeStatusEnum.ACTIVE;

    @Column(name = "value",nullable = false,length = 2048,unique = true)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "ticket_id",nullable = false)
    private Ticket ticket;

    @Version
    private Long version;

    // INVARIANT: Canon - Only one QR code may be ACTIVE at a time.
    public void revoke() {
        this.status = QrCodeStatusEnum.REVOKED;
    }

    public boolean isActive() {
        return this.status == QrCodeStatusEnum.ACTIVE;
    }

    void setTicket(@NonNull Ticket ticket) {
        this.ticket = ticket;
    }
}

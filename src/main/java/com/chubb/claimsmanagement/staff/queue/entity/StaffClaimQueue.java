package com.chubb.claimsmanagement.staff.queue.entity;

import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.staff.entity.Staff;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "staff_claim_queue")
@Getter
@Setter
@NoArgsConstructor
public class StaffClaimQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false, unique = true)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status;

    @Column(nullable = false)
    private LocalDateTime queuedAt;

    private LocalDateTime pickedUpAt;

    @PrePersist
    protected void onCreate() {
        if (queuedAt == null) {
            queuedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = QueueStatus.AVAILABLE;
        }
    }
}
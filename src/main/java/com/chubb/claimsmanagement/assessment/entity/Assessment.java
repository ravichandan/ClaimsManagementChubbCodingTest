package com.chubb.claimsmanagement.assessment.entity;

import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.common.enums.AssessmentResult;
import com.chubb.claimsmanagement.staff.entity.Staff;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(nullable = false, length = 500)
    private String assessmentType;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(length = 2000)
    private String details;

    @Column(nullable = false)
    private Double estimatedAmount;

    @Column
    private Double settledAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentResult result;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

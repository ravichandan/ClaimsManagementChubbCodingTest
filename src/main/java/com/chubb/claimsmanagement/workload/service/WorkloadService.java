package com.chubb.claimsmanagement.workload.service;

import com.chubb.claimsmanagement.assessment.entity.Assessment;
import com.chubb.claimsmanagement.assessment.repository.AssessmentRepository;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.common.enums.AssessmentResult;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.workload.dto.OfficerWorkload;
import com.chubb.claimsmanagement.workload.dto.WorkloadSummaryResponse;
import com.chubb.claimsmanagement.staff.entity.Staff;
import com.chubb.claimsmanagement.staff.repository.StaffRepository;
import com.chubb.claimsmanagement.staff.queue.entity.QueueStatus;
import com.chubb.claimsmanagement.staff.queue.entity.StaffClaimQueue;
import com.chubb.claimsmanagement.staff.queue.repository.StaffClaimQueueRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Service
/** Builds operational workload summaries from claims, assessments, and queues. */
public class WorkloadService {

        private static final Logger log = LoggerFactory.getLogger(WorkloadService.class);

    private final ClaimRepository claimRepository;
    private final AssessmentRepository assessmentRepository;
    private final StaffRepository staffRepository;
    private final StaffClaimQueueRepository staffClaimQueueRepository;

    public WorkloadService(ClaimRepository claimRepository,
                           AssessmentRepository assessmentRepository,
                           StaffRepository staffRepository,
                           StaffClaimQueueRepository staffClaimQueueRepository) {
        this.claimRepository = claimRepository;
        this.assessmentRepository = assessmentRepository;
        this.staffRepository = staffRepository;
        this.staffClaimQueueRepository = staffClaimQueueRepository;
    }

    public WorkloadSummaryResponse getWorkloadSummary() {
        log.debug("Building combined workload and liability summary");
        List<com.chubb.claimsmanagement.claim.entity.Claim> claims = claimRepository.findAll();
        List<StaffClaimQueue> pickedUpClaims = staffClaimQueueRepository
                .findByStatusOrderByQueuedAtAsc(QueueStatus.PICKED_UP);
        List<StaffClaimQueue> availableClaims = staffClaimQueueRepository
                .findByStatusOrderByQueuedAtAsc(QueueStatus.AVAILABLE);

        int assignedClaims = pickedUpClaims.size();
        int underAssessment = (int) claims.stream()
                .filter(claim -> claim.getStatus() == ClaimStatus.ASSESSMENT_IN_PROGRESS)
                .count();
        int unassignedClaims = availableClaims.size();
        int outstandingClaims = (int) claims.stream()
                .filter(claim -> claim.getStatus() != ClaimStatus.APPROVED
                        && claim.getStatus() != ClaimStatus.REJECTED
                        && claim.getStatus() != ClaimStatus.CLOSED)
                .count();

        return new WorkloadSummaryResponse(
                claims.size(),
                calculateLiabilityExposure(),
                assignedClaims,
                underAssessment,
                unassignedClaims,
                outstandingClaims,
                staffRepository.findAll().stream()
                        .map(staff -> toOfficerWorkload(staff, pickedUpClaims))
                        .toList()
        );
    }

    private OfficerWorkload toOfficerWorkload(Staff staff, List<StaffClaimQueue> pickedUpClaims) {
        List<com.chubb.claimsmanagement.claim.entity.Claim> officerClaims = pickedUpClaims.stream()
                .filter(queue -> queue.getStaff().getId().equals(staff.getId()))
                .map(StaffClaimQueue::getClaim)
                .toList();
        int underAssessment = (int) officerClaims.stream()
                .filter(claim -> claim.getStatus() == ClaimStatus.ASSESSMENT_IN_PROGRESS)
                .count();
        return new OfficerWorkload(
                staff.getStaffNumber(),
                officerClaims.size(),
                underAssessment,
                officerClaims.stream()
                        .map(claim -> claim.getClaimNumber())
                        .toList()
        );
    }

    private BigDecimal calculateLiabilityExposure() {
        List<Assessment> assessments = assessmentRepository.findAll();
        BigDecimal totalRequestedAmount = assessments.stream()
                .map(Assessment::getEstimatedAmount)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalApprovedAmount = assessments.stream()
                .filter(assessment -> assessment.getResult() == AssessmentResult.APPROVED)
                .map(Assessment::getEstimatedAmount)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalRequestedAmount.subtract(totalApprovedAmount);
        }
}

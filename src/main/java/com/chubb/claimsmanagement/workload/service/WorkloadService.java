package com.chubb.claimsmanagement.workload.service;

import com.chubb.claimsmanagement.assessment.entity.Assessment;
import com.chubb.claimsmanagement.assessment.repository.AssessmentRepository;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.common.enums.AssessmentResult;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.workload.dto.LiabilityExposureResponse;
import com.chubb.claimsmanagement.workload.dto.AssignedClaimSummary;
import com.chubb.claimsmanagement.workload.dto.StaffAssignmentSummary;
import com.chubb.claimsmanagement.workload.dto.WorkloadSummary;
import com.chubb.claimsmanagement.workload.dto.WorkloadSummaryResponse;
import com.chubb.claimsmanagement.staff.entity.Staff;
import com.chubb.claimsmanagement.staff.repository.StaffRepository;
import com.chubb.claimsmanagement.staff.queue.entity.QueueStatus;
import com.chubb.claimsmanagement.staff.queue.entity.StaffClaimQueue;
import com.chubb.claimsmanagement.staff.queue.repository.StaffClaimQueueRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class WorkloadService {

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
        List<WorkloadSummary> statusSummary = Arrays.stream(ClaimStatus.values())
                .map(status -> {
                    List<String> claimNumbers = claimRepository.findByStatus(status).stream()
                            .map(claim -> claim.getClaimNumber())
                            .toList();
                    return new WorkloadSummary(status, claimNumbers.size(), claimNumbers);
                })
                .toList();

        List<StaffAssignmentSummary> staffAssignments = staffRepository.findAll().stream()
                .map(this::toStaffAssignmentSummary)
                .toList();

        return new WorkloadSummaryResponse(statusSummary, staffAssignments);
    }

    private StaffAssignmentSummary toStaffAssignmentSummary(Staff staff) {
        List<AssignedClaimSummary> assignedClaims = staffClaimQueueRepository
                .findByStaffIdAndStatusOrderByPickedUpAtAsc(staff.getId(), QueueStatus.PICKED_UP)
                .stream()
                .map(StaffClaimQueue::getClaim)
                .map(claim -> new AssignedClaimSummary(claim.getClaimNumber(), claim.getStatus()))
                .toList();
        return new StaffAssignmentSummary(staff.getStaffNumber(), assignedClaims);
    }

    public LiabilityExposureResponse getLiabilityExposure() {
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

        return new LiabilityExposureResponse(
                totalRequestedAmount,
                totalApprovedAmount,
                totalRequestedAmount.subtract(totalApprovedAmount)
        );
    }
}

package com.chubb.claimsmanagement.workload.service;

import com.chubb.claimsmanagement.assessment.entity.Assessment;
import com.chubb.claimsmanagement.assessment.repository.AssessmentRepository;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.common.enums.AssessmentResult;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.workload.dto.LiabilityExposureResponse;
import com.chubb.claimsmanagement.workload.dto.WorkloadSummary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class WorkloadService {

    private final ClaimRepository claimRepository;
    private final AssessmentRepository assessmentRepository;

    public WorkloadService(ClaimRepository claimRepository, AssessmentRepository assessmentRepository) {
        this.claimRepository = claimRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public List<WorkloadSummary> getWorkloadSummary() {
        return Arrays.stream(ClaimStatus.values())
                .map(status -> new WorkloadSummary(status, claimRepository.findByStatus(status).size()))
                .toList();
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

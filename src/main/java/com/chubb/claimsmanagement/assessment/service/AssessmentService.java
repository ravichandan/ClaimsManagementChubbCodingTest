package com.chubb.claimsmanagement.assessment.service;

import com.chubb.claimsmanagement.assessment.dto.AssessmentResponse;
import com.chubb.claimsmanagement.assessment.dto.CreateAssessmentRequest;
import com.chubb.claimsmanagement.assessment.entity.Assessment;
import com.chubb.claimsmanagement.assessment.repository.AssessmentRepository;
import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.exceptions.ResourceNotFoundException;
import com.chubb.claimsmanagement.staff.entity.Staff;
import com.chubb.claimsmanagement.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final ClaimRepository claimRepository;
    private final StaffRepository staffRepository;

    public AssessmentService(AssessmentRepository assessmentRepository,
                             ClaimRepository claimRepository,
                             StaffRepository staffRepository) {
        this.assessmentRepository = assessmentRepository;
        this.claimRepository = claimRepository;
        this.staffRepository = staffRepository;
    }

    public AssessmentResponse createAssessment(CreateAssessmentRequest request) {
        Claim claim = claimRepository.findByClaimNumber(request.claimNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + request.claimNumber()));

        Staff staff = staffRepository.findByStaffNumber(request.staffNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + request.staffNumber()));

        Assessment assessment = new Assessment();
        assessment.setClaim(claim);
        assessment.setStaff(staff);
        assessment.setAssessmentType(request.assessmentType());
        assessment.setDescription(request.description());
        assessment.setDetails(request.details());
        assessment.setEstimatedAmount(request.estimatedAmount());
        assessment.setResult(request.result());

        // Update claim status to ASSESSMENT_IN_PROGRESS
        claim.setStatus(ClaimStatus.ASSESSMENT_IN_PROGRESS);
        claimRepository.save(claim);

        Assessment saved = assessmentRepository.save(assessment);
        return toResponse(saved);
    }

    public AssessmentResponse getAssessment(UUID assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + assessmentId));
    }

        public List<AssessmentResponse> getAssessmentsByClaim(String claimNumber) {
                Claim claim = claimRepository.findByClaimNumber(claimNumber)
                                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));

                return assessmentRepository.findByClaimId(claim.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

        public List<AssessmentResponse> getAssessmentsByStaff(String staffNumber) {
                Staff staff = staffRepository.findByStaffNumber(staffNumber)
                                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffNumber));

                return assessmentRepository.findByStaffId(staff.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AssessmentResponse updateSettledAmount(UUID assessmentId, Double settledAmount) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + assessmentId));

        assessment.setSettledAmount(settledAmount);
        Assessment saved = assessmentRepository.save(assessment);
        return toResponse(saved);
    }

    private AssessmentResponse toResponse(Assessment assessment) {
        return new AssessmentResponse(
                assessment.getId(),
                assessment.getClaim().getClaimNumber(),
                assessment.getStaff().getStaffNumber(),
                assessment.getAssessmentType(),
                assessment.getDescription(),
                assessment.getDetails(),
                assessment.getEstimatedAmount(),
                assessment.getSettledAmount(),
                assessment.getResult(),
                assessment.getCreatedAt(),
                assessment.getUpdatedAt()
        );
    }
}

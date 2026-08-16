package com.chubb.claimsmanagement.assessment.service;

import com.chubb.claimsmanagement.assessment.dto.AssessmentResponse;
import com.chubb.claimsmanagement.assessment.dto.CreateAssessmentRequest;
import com.chubb.claimsmanagement.assessment.entity.Assessment;
import com.chubb.claimsmanagement.assessment.repository.AssessmentRepository;
import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.claim.dto.ClaimResponse;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.common.exceptions.BadRequestException;
import com.chubb.claimsmanagement.common.enums.AssessmentResult;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.events.AssessmentApprovedEvent;
import com.chubb.claimsmanagement.common.events.AssessmentRejectedEvent;
import com.chubb.claimsmanagement.common.exceptions.ResourceNotFoundException;
import com.chubb.claimsmanagement.notification.service.NotificationService;
import com.chubb.claimsmanagement.staff.entity.Staff;
import com.chubb.claimsmanagement.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final ClaimRepository claimRepository;
    private final StaffRepository staffRepository;
    private final NotificationService notificationService;

    public AssessmentService(AssessmentRepository assessmentRepository,
                             ClaimRepository claimRepository,
                             StaffRepository staffRepository,
                             NotificationService notificationService) {
        this.assessmentRepository = assessmentRepository;
        this.claimRepository = claimRepository;
        this.staffRepository = staffRepository;
        this.notificationService = notificationService;
    }

    @Transactional
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

        claim.setStatus(toClaimStatus(request.result()));
        claimRepository.save(claim);

        Assessment saved = assessmentRepository.save(assessment);
        if (request.result() == AssessmentResult.APPROVED) {
            notificationService.publishAssessmentApproved(new AssessmentApprovedEvent(
                saved.getId(),
                claim.getId(),
                claim.getClaimNumber(),
                staff.getStaffNumber(),
                saved.getEstimatedAmount(),
                saved.getSettledAmount()
            ));
            } else if (request.result() == AssessmentResult.REJECTED || request.result() == AssessmentResult.MORE_INFO_REQUIRED) {
                notificationService.publishAssessmentRejected(new AssessmentRejectedEvent(
                    saved.getId(),
                    claim.getId(),
                    claim.getClaimNumber(),
                    claim.getClaimant().getFirstName(),
                    claim.getClaimant().getEmail(),
                    request.result(),
                    saved.getDescription()
                ));
        }
        return toResponse(saved);
    }

    public ClaimResponse startAssessment(String claimNumber, String staffNumber) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));

        staffRepository.findByStaffNumber(staffNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffNumber));

        if (claim.getStatus() != ClaimStatus.ASSIGNED
                && claim.getStatus() != ClaimStatus.MORE_INFO_REQUESTED) {
            throw new BadRequestException("Assessment cannot be started in status: " + claim.getStatus());
        }

        claim.setStatus(ClaimStatus.ASSESSMENT_IN_PROGRESS);
        Claim saved = claimRepository.save(claim);
        return toClaimResponse(saved);
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

    private ClaimStatus toClaimStatus(AssessmentResult result) {
        return switch (result) {
            case APPROVED -> ClaimStatus.APPROVED;
            case REJECTED -> ClaimStatus.REJECTED;
            case MORE_INFO_REQUIRED -> ClaimStatus.MORE_INFO_REQUESTED;
        };
    }

    private ClaimResponse toClaimResponse(Claim claim) {
        return new ClaimResponse(
                claim.getClaimNumber(),
                claim.getClaimant().getClaimantMemberNumber(),
                claim.getClaimType(),
                claim.getStatus(),
                claim.getDescription(),
                claim.getCreatedAt(),
                claim.getUpdatedAt()
        );
    }
}

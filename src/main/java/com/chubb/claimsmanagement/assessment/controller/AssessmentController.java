package com.chubb.claimsmanagement.assessment.controller;

import com.chubb.claimsmanagement.assessment.dto.AssessmentResponse;
import com.chubb.claimsmanagement.assessment.dto.CreateAssessmentRequest;
import com.chubb.claimsmanagement.assessment.service.AssessmentService;
import com.chubb.claimsmanagement.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

        @PostMapping("/claims/{claimNumber}/assessments")
    public ResponseEntity<ApiResponse<AssessmentResponse>> createAssessment(
            @PathVariable String claimNumber,
            @Valid @RequestBody CreateAssessmentRequest request) {
        AssessmentResponse response = assessmentService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Assessment created successfully"));
    }

    @GetMapping("/assessments/{assessmentId}")
    public ResponseEntity<ApiResponse<AssessmentResponse>> getAssessment(@PathVariable UUID assessmentId) {
        return ResponseEntity.ok(ApiResponse.success(assessmentService.getAssessment(assessmentId)));
    }

    @GetMapping("/claims/{claimNumber}/assessments")
    public ResponseEntity<ApiResponse<List<AssessmentResponse>>> getAssessmentsByClaim(@PathVariable String claimNumber) {
        return ResponseEntity.ok(ApiResponse.success(assessmentService.getAssessmentsByClaim(claimNumber)));
    }

    @GetMapping("/staff/{staffNumber}/assessments")
    public ResponseEntity<ApiResponse<List<AssessmentResponse>>> getAssessmentsByStaff(@PathVariable String staffNumber) {
        return ResponseEntity.ok(ApiResponse.success(assessmentService.getAssessmentsByStaff(staffNumber)));
    }

    @PatchMapping("/assessments/{assessmentId}/settlement")
    public ResponseEntity<ApiResponse<AssessmentResponse>> updateSettledAmount(
            @PathVariable UUID assessmentId,
            @RequestParam Double settledAmount) {
        return ResponseEntity.ok(ApiResponse.success(
                assessmentService.updateSettledAmount(assessmentId, settledAmount),
                "Settled amount updated successfully"
        ));
    }
}

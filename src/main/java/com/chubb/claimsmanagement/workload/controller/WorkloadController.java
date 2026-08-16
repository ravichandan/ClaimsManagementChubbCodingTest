package com.chubb.claimsmanagement.workload.controller;

import com.chubb.claimsmanagement.common.api.ApiResponse;
import com.chubb.claimsmanagement.workload.dto.WorkloadSummary;
import com.chubb.claimsmanagement.workload.dto.LiabilityExposureResponse;
import com.chubb.claimsmanagement.workload.service.WorkloadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class WorkloadController {

    private final WorkloadService workloadService;

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @GetMapping("/management/claims")
    public ResponseEntity<ApiResponse<List<WorkloadSummary>>> getWorkloadSummary() {
        return ResponseEntity.ok(ApiResponse.success(workloadService.getWorkloadSummary()));
    }

    @GetMapping("/management/liability-exposure")
    public ResponseEntity<ApiResponse<LiabilityExposureResponse>> getLiabilityExposure() {
        return ResponseEntity.ok(ApiResponse.success(workloadService.getLiabilityExposure()));
    }
}

package com.chubb.claimsmanagement.staff.controller;

import com.chubb.claimsmanagement.common.api.ApiResponse;
import com.chubb.claimsmanagement.staff.dto.StaffResponse;
import com.chubb.claimsmanagement.staff.queue.dto.StaffClaimQueueResponse;
import com.chubb.claimsmanagement.staff.queue.service.StaffClaimQueueService;
import com.chubb.claimsmanagement.staff.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StaffController {

    private final StaffService staffService;
    private final StaffClaimQueueService queueService;

    public StaffController(StaffService staffService, StaffClaimQueueService queueService) {
        this.staffService = staffService;
        this.queueService = queueService;
    }

    @GetMapping("/staff")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getStaff() {
        return ResponseEntity.ok(ApiResponse.success(staffService.getStaff()));
    }

    @GetMapping("/staff/{staffNumber}")
    public ResponseEntity<ApiResponse<StaffResponse>> getStaff(@PathVariable String staffNumber) {
        return ResponseEntity.ok(ApiResponse.success(staffService.getStaff(staffNumber)));
    }

    @GetMapping("/staff/claims/queue")
    public ResponseEntity<ApiResponse<List<StaffClaimQueueResponse>>> getAvailableClaims() {
        return ResponseEntity.ok(ApiResponse.success(queueService.getAvailableClaims()));
    }

    @GetMapping("/staff/{staffNumber}/claims/queue")
    public ResponseEntity<ApiResponse<List<StaffClaimQueueResponse>>> getClaimsForStaff(@PathVariable String staffNumber) {
        return ResponseEntity.ok(ApiResponse.success(queueService.getClaimsForStaff(staffNumber)));
    }

    @PostMapping("/staff/{staffNumber}/claims/queue/{claimNumber}/pickup")
    public ResponseEntity<ApiResponse<StaffClaimQueueResponse>> pickUpClaim(
            @PathVariable String staffNumber, @PathVariable String claimNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                queueService.pickUpClaim(claimNumber, staffNumber), "Claim picked up successfully"));
    }

    @PostMapping("/staff/{staffNumber}/claims/queue/{claimNumber}/requeue")
    public ResponseEntity<ApiResponse<StaffClaimQueueResponse>> requeueClaim(
            @PathVariable String staffNumber, @PathVariable String claimNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                queueService.requeueClaim(claimNumber, staffNumber), "Claim requeued successfully"));
    }
}
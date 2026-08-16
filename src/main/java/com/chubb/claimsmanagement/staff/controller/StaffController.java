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
/** REST endpoints for staff lookup and claim queue operations. */
public class StaffController {

    private final StaffService staffService;
    private final StaffClaimQueueService queueService;

    public StaffController(StaffService staffService, StaffClaimQueueService queueService) {
        this.staffService = staffService;
        this.queueService = queueService;
    }

    @GetMapping("/staff")
    /** Lists staff members using their public staff numbers. */
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getStaff() {
        return ResponseEntity.ok(ApiResponse.success(staffService.getStaff()));
    }

    @GetMapping("/staff/{staffNumber}")
    /** Retrieves one staff member by public business number. */
    public ResponseEntity<ApiResponse<StaffResponse>> getStaff(@PathVariable String staffNumber) {
        return ResponseEntity.ok(ApiResponse.success(staffService.getStaff(staffNumber)));
    }

    @GetMapping("/staff/claims/queue")
    /** Lists claims currently available for pickup. */
    public ResponseEntity<ApiResponse<List<StaffClaimQueueResponse>>> getAvailableClaims() {
        return ResponseEntity.ok(ApiResponse.success(queueService.getAvailableClaims()));
    }

    @GetMapping("/staff/{staffNumber}/claims/queue")
    /** Lists claims currently picked up by a staff member. */
    public ResponseEntity<ApiResponse<List<StaffClaimQueueResponse>>> getClaimsForStaff(@PathVariable String staffNumber) {
        return ResponseEntity.ok(ApiResponse.success(queueService.getClaimsForStaff(staffNumber)));
    }

    @PostMapping("/staff/{staffNumber}/claims/queue/{claimNumber}/pickup")
    /** Atomically picks up an available claim for a staff member. */
    public ResponseEntity<ApiResponse<StaffClaimQueueResponse>> pickUpClaim(
            @PathVariable String staffNumber, @PathVariable String claimNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                queueService.pickUpClaim(claimNumber, staffNumber), "Claim picked up successfully"));
    }

    @PostMapping("/staff/{staffNumber}/claims/queue/{claimNumber}/requeue")
    /** Returns a staff-owned claim to the available queue. */
    public ResponseEntity<ApiResponse<StaffClaimQueueResponse>> requeueClaim(
            @PathVariable String staffNumber, @PathVariable String claimNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                queueService.requeueClaim(claimNumber, staffNumber), "Claim requeued successfully"));
    }
}
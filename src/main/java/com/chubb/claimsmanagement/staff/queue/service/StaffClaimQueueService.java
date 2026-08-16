package com.chubb.claimsmanagement.staff.queue.service;

import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.exceptions.BadRequestException;
import com.chubb.claimsmanagement.common.exceptions.ResourceNotFoundException;
import com.chubb.claimsmanagement.staff.entity.Staff;
import com.chubb.claimsmanagement.staff.queue.dto.StaffClaimQueueResponse;
import com.chubb.claimsmanagement.staff.queue.entity.QueueStatus;
import com.chubb.claimsmanagement.staff.queue.entity.StaffClaimQueue;
import com.chubb.claimsmanagement.staff.queue.repository.StaffClaimQueueRepository;
import com.chubb.claimsmanagement.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StaffClaimQueueService {

    private final StaffClaimQueueRepository queueRepository;
    private final ClaimRepository claimRepository;
    private final StaffRepository staffRepository;

    public StaffClaimQueueService(StaffClaimQueueRepository queueRepository,
                                  ClaimRepository claimRepository,
                                  StaffRepository staffRepository) {
        this.queueRepository = queueRepository;
        this.claimRepository = claimRepository;
        this.staffRepository = staffRepository;
    }

    @Transactional
    public void enqueue(UUID claimId) {
        if (queueRepository.findByClaimId(claimId).isPresent()) {
            return;
        }

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimId));
        StaffClaimQueue queue = new StaffClaimQueue();
        queue.setClaim(claim);
        queue.setStatus(QueueStatus.AVAILABLE);
        queueRepository.save(queue);
    }

    @Transactional(readOnly = true)
    public List<StaffClaimQueueResponse> getAvailableClaims() {
        return queueRepository.findByStatusOrderByQueuedAtAsc(QueueStatus.AVAILABLE)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<StaffClaimQueueResponse> getClaimsForStaff(String staffNumber) {
        Staff staff = requireStaff(staffNumber);
        return queueRepository.findByStaffIdAndStatusOrderByPickedUpAtAsc(staff.getId(), QueueStatus.PICKED_UP)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public StaffClaimQueueResponse pickUpClaim(String claimNumber, String staffNumber) {
        Staff staff = requireStaff(staffNumber);
        Claim claim = requireClaim(claimNumber);
        StaffClaimQueue queue = queueRepository.findByClaimIdAndStatusForUpdate(claim.getId(), QueueStatus.AVAILABLE)
                .orElseThrow(() -> new ResourceNotFoundException("Available claim not found: " + claimNumber));

        claim = queue.getClaim();
        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new BadRequestException("Claim cannot be picked up in status: " + claim.getStatus());
        }

        queue.setStaff(staff);
        queue.setStatus(QueueStatus.PICKED_UP);
        queue.setPickedUpAt(LocalDateTime.now());
        claim.setStatus(ClaimStatus.ASSIGNED);
        claimRepository.save(claim);
        return toResponse(queueRepository.save(queue));
    }

    @Transactional
    public StaffClaimQueueResponse requeueClaim(String claimNumber, String staffNumber) {
        Staff staff = requireStaff(staffNumber);
        Claim claim = requireClaim(claimNumber);
        StaffClaimQueue queue = queueRepository.findByClaimIdAndStatusForUpdate(claim.getId(), QueueStatus.PICKED_UP)
                .orElseThrow(() -> new ResourceNotFoundException("Picked up claim not found: " + claimNumber));

        if (!staff.getId().equals(queue.getStaff().getId())) {
            throw new BadRequestException("Claim is assigned to another staff member");
        }

        queue.setStaff(null);
        queue.setStatus(QueueStatus.AVAILABLE);
        queue.setPickedUpAt(null);
        queue.getClaim().setStatus(ClaimStatus.SUBMITTED);
        claimRepository.save(queue.getClaim());
        return toResponse(queueRepository.save(queue));
    }

    private Staff requireStaff(String staffNumber) {
        return staffRepository.findByStaffNumber(staffNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffNumber));
    }

    private Claim requireClaim(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));
    }

    private StaffClaimQueueResponse toResponse(StaffClaimQueue queue) {
        Claim claim = queue.getClaim();
        return new StaffClaimQueueResponse(
                claim.getClaimNumber(),
                claim.getClaimant().getClaimantMemberNumber(),
                claim.getClaimType(),
                claim.getStatus(),
                queue.getStatus(),
                queue.getStaff() == null ? null : queue.getStaff().getStaffNumber(),
                queue.getQueuedAt(),
                queue.getPickedUpAt()
        );
    }
}
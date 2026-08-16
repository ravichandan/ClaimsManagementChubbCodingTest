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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
/** Manages durable staff claim queue state and pickup transitions. */
public class StaffClaimQueueService {

    private static final Logger log = LoggerFactory.getLogger(StaffClaimQueueService.class);

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
    /** Creates an available queue entry once for a newly submitted claim. */
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
    /** Makes a picked-up claim available again after claimant information arrives. */
    public void makeAvailableAfterMoreInformation(UUID claimId) {
        StaffClaimQueue queue = queueRepository.findByClaimIdAndStatusForUpdate(claimId, QueueStatus.PICKED_UP)
                .orElseThrow(() -> new ResourceNotFoundException("Picked up claim not found: " + claimId));

        queue.setStaff(null);
        queue.setStatus(QueueStatus.AVAILABLE);
        queue.setPickedUpAt(null);
        queueRepository.save(queue);
        log.info("Claim {} returned to the available staff queue", queue.getClaim().getClaimNumber());
    }

    @Transactional
    /** Assigns an available claim to a staff member under a row lock. */
    public StaffClaimQueueResponse pickUpClaim(String claimNumber, String staffNumber) {
        Staff staff = requireStaff(staffNumber);
        Claim claim = requireClaim(claimNumber);
        StaffClaimQueue queue = queueRepository.findByClaimIdAndStatusForUpdate(claim.getId(), QueueStatus.AVAILABLE)
                .orElseThrow(() -> new ResourceNotFoundException("Available claim not found: " + claimNumber));

        claim = queue.getClaim();
        if (claim.getStatus() != ClaimStatus.SUBMITTED
            && claim.getStatus() != ClaimStatus.MORE_INFO_PROVIDED) {
            throw new BadRequestException("Claim cannot be picked up in status: " + claim.getStatus());
        }

        queue.setStaff(staff);
        queue.setStatus(QueueStatus.PICKED_UP);
        queue.setPickedUpAt(LocalDateTime.now());
        claim.setStatus(ClaimStatus.ASSIGNED);
        claimRepository.save(claim);
        log.info("Claim {} picked up by staff {}", claimNumber, staffNumber);
        return toResponse(queueRepository.save(queue));
    }

    @Transactional
    /** Requeues a claim only when the requesting staff member owns it. */
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
        log.info("Claim {} requeued by staff {}", claimNumber, staffNumber);
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
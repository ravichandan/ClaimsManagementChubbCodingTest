package com.chubb.claimsmanagement.staff.queue.repository;

import com.chubb.claimsmanagement.staff.queue.entity.QueueStatus;
import com.chubb.claimsmanagement.staff.queue.entity.StaffClaimQueue;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Persistence and locked lookup operations for staff queue entries. */
public interface StaffClaimQueueRepository extends JpaRepository<StaffClaimQueue, UUID> {
    Optional<StaffClaimQueue> findByClaimId(UUID claimId);

    List<StaffClaimQueue> findByStatusOrderByQueuedAtAsc(QueueStatus status);

    List<StaffClaimQueue> findByStaffIdAndStatusOrderByPickedUpAtAsc(UUID staffId, QueueStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select queue from StaffClaimQueue queue where queue.claim.id = :claimId and queue.status = :status")
    Optional<StaffClaimQueue> findByClaimIdAndStatusForUpdate(
            @Param("claimId") UUID claimId,
            @Param("status") QueueStatus status);
}
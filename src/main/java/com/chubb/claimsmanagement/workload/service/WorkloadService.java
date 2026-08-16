package com.chubb.claimsmanagement.workload.service;

import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.workload.dto.WorkloadSummary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class WorkloadService {

    private final ClaimRepository claimRepository;

    public WorkloadService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public List<WorkloadSummary> getWorkloadSummary() {
        return Arrays.stream(ClaimStatus.values())
                .map(status -> new WorkloadSummary(status, claimRepository.findByStatus(status).size()))
                .toList();
    }
}

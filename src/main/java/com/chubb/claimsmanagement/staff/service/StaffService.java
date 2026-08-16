package com.chubb.claimsmanagement.staff.service;

import com.chubb.claimsmanagement.common.exceptions.ResourceNotFoundException;
import com.chubb.claimsmanagement.staff.dto.StaffResponse;
import com.chubb.claimsmanagement.staff.entity.Staff;
import com.chubb.claimsmanagement.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/** Provides staff lookup operations for public API boundaries. */
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<StaffResponse> getStaff() {
        return staffRepository.findAll().stream().map(this::toResponse).toList();
    }

    public StaffResponse getStaff(String staffNumber) {
        return staffRepository.findByStaffNumber(staffNumber)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffNumber));
    }

    private StaffResponse toResponse(Staff staff) {
        return new StaffResponse(
                staff.getStaffNumber(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getRole(),
                staff.getCreatedAt()
        );
    }
}
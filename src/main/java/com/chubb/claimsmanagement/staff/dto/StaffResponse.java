package com.chubb.claimsmanagement.staff.dto;

import java.time.LocalDateTime;
public record StaffResponse(
        String staffNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        LocalDateTime createdAt
) {
}
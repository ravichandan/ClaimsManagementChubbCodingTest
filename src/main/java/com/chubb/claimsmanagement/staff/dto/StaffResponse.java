package com.chubb.claimsmanagement.staff.dto;

import java.time.LocalDateTime;
/** Public staff representation; internal UUIDs are intentionally omitted. */
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
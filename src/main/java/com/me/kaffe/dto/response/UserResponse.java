package com.me.kaffe.dto.response;

import com.me.kaffe.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * UserResponse DTO
 * Used for returning user information without sensitive data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private UUID uniqueId;
    private String username;
    private String fullName;
    private String number;
    private Role role;

    /**
     * Check if user has admin role
     */
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    /**
     * Check if user has customer role
     */
    public boolean isCustomer() {
        return this.role == Role.CUSTOMER;
    }

    /**
     * Check if user has employee role
     */
    public boolean isEmployee() {
        return this.role == Role.EMPLOYEE;
    }
}


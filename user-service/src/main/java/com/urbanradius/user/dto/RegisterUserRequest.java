package com.urbanradius.user.dto;

import com.urbanradius.common.dto.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserRequest(
        @NotBlank @Email String email,
        @NotBlank String fullName,
        @NotBlank String phone,
        @NotBlank String city,
        @NotNull UserRole role
) {
}

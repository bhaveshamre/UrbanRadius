package com.urbanradius.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RateUserRequest(
        @NotNull UUID raterId,
        @NotNull @Min(1) @Max(5) Integer score
) {
}

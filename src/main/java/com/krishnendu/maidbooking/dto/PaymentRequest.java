package com.krishnendu.maidbooking.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentRequest(@NotBlank String method,
                             @NotBlank String details) {
}

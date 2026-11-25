package com.krishnendu.maidbooking.dto;

import com.krishnendu.maidbooking.model.BookingType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingRequest(@NotBlank String maidId,
                             @NotNull BookingType type,
                             LocalDateTime start,
                             @Min(1) @Max(12) int hours) {
}

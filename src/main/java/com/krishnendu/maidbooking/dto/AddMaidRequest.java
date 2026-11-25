package com.krishnendu.maidbooking.dto;

import com.krishnendu.maidbooking.model.Maid;
import com.krishnendu.maidbooking.model.ServiceType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;
import java.util.Set;

public record AddMaidRequest(@NotBlank String name,
                             @NotBlank String locality,
                             @NotEmpty Set<ServiceType> services,
                             @Positive double pricePerHour,
                             @DecimalMin("0") @DecimalMax("5") double rating,
                             @NotNull LocalTime workStart,
                             @NotNull LocalTime workEnd) {

    public Maid toMaid() {
        return new Maid(name, locality, services, pricePerHour, rating, workStart, workEnd);
    }
}

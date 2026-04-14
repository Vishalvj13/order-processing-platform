package com.vishal.ordering.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank String productCode,
        @NotNull @Positive Integer quantity,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Email String customerEmail
) {
}

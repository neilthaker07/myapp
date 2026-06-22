package com.demo.delivery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class RegisterDriverRequest {
    @NotBlank String driverId;
    @NotNull @DecimalMin("0.01") BigDecimal hourlyRate;
}

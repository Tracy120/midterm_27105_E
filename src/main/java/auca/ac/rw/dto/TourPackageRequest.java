package auca.ac.rw.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record TourPackageRequest(
        @NotBlank @Size(max = 30) String packageCode,
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 1000) String description,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull @Positive Integer durationInDays,
        Set<Long> guideIds
) {
}

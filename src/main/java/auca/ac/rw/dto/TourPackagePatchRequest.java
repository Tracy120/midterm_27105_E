package auca.ac.rw.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record TourPackagePatchRequest(
        @Size(max = 30) String packageCode,
        @Size(max = 120) String title,
        @Size(max = 1000) String description,
        @DecimalMin("0.0") BigDecimal price,
        @Positive Integer durationInDays,
        Set<Long> guideIds
) {
}

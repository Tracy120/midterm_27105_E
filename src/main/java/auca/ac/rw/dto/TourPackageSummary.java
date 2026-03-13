package auca.ac.rw.dto;

import java.math.BigDecimal;

public record TourPackageSummary(
        Long id,
        String packageCode,
        String title,
        BigDecimal price,
        Integer durationInDays
) {
}

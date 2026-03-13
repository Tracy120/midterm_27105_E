package auca.ac.rw.dto;

import java.math.BigDecimal;
import java.util.List;

public record TourPackageResponse(
        Long id,
        String packageCode,
        String title,
        String description,
        BigDecimal price,
        Integer durationInDays,
        List<TourGuideResponse> guides
) {
}

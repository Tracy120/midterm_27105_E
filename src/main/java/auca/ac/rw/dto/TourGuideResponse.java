package auca.ac.rw.dto;

public record TourGuideResponse(
        Long id,
        String guideCode,
        String fullName,
        String email,
        String phoneNumber,
        String specialization
) {
}

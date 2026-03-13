package auca.ac.rw.dto;

public record UserProfileResponse(
        Long id,
        String nationalId,
        String gender,
        String emergencyContact,
        String passportNumber
) {
}

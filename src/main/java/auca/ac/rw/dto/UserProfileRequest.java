package auca.ac.rw.dto;

import jakarta.validation.constraints.Size;

public record UserProfileRequest(
        @Size(max = 30) String nationalId,
        @Size(max = 20) String gender,
        @Size(max = 80) String emergencyContact,
        @Size(max = 30) String passportNumber
) {
}

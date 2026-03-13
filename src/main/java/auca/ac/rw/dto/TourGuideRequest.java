package auca.ac.rw.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TourGuideRequest(
        @NotBlank @Size(max = 30) String guideCode,
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 30) String phoneNumber,
        @Size(max = 120) String specialization
) {
}

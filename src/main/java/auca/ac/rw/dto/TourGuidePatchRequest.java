package auca.ac.rw.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record TourGuidePatchRequest(
        @Size(max = 30) String guideCode,
        @Size(max = 120) String fullName,
        @Email @Size(max = 120) String email,
        @Size(max = 30) String phoneNumber,
        @Size(max = 120) String specialization
) {
}

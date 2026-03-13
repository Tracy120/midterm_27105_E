package auca.ac.rw.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank @Size(max = 30) String userCode,
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 30) String phoneNumber,
        @Size(max = 30) String villageCode,
        @Size(max = 120) String villageName,
        @Valid UserProfileRequest profile
) {
}

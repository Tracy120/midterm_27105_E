package auca.ac.rw.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserPatchRequest(
        @Size(max = 30) String userCode,
        @Size(max = 80) String firstName,
        @Size(max = 80) String lastName,
        @Email @Size(max = 120) String email,
        @Size(max = 30) String phoneNumber,
        @Size(max = 30) String villageCode,
        @Size(max = 120) String villageName,
        @Valid UserProfilePatchRequest profile
) {
}

package auca.ac.rw.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String userCode,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocationHierarchyResponse location,
        UserProfileResponse profile,
        LocalDateTime createdAt
) {
}

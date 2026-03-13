package auca.ac.rw.dto;

public record UserBookingSummary(
        Long id,
        String userCode,
        String fullName,
        String email,
        String villageName,
        String provinceName
) {
}

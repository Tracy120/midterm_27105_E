package auca.ac.rw.dto;

public record DistrictResponse(
        Long id,
        String code,
        String name,
        String provinceCode,
        String provinceName
) {
}

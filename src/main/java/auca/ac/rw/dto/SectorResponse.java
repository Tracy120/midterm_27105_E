package auca.ac.rw.dto;

public record SectorResponse(
        Long id,
        String code,
        String name,
        String districtCode,
        String districtName,
        String provinceCode,
        String provinceName
) {
}

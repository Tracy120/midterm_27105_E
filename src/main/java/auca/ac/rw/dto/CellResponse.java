package auca.ac.rw.dto;

public record CellResponse(
        Long id,
        String code,
        String name,
        String sectorCode,
        String sectorName,
        String districtCode,
        String districtName,
        String provinceCode,
        String provinceName
) {
}

package auca.ac.rw.dto;

public record VillageResponse(
        Long id,
        String code,
        String name,
        String cellCode,
        String cellName,
        String sectorCode,
        String sectorName,
        String districtCode,
        String districtName,
        String provinceCode,
        String provinceName
) {
}

package auca.ac.rw.dto;

public record LocationHierarchyResponse(
        String provinceCode,
        String provinceName,
        String districtCode,
        String districtName,
        String sectorCode,
        String sectorName,
        String cellCode,
        String cellName,
        String villageCode,
        String villageName
) {
}

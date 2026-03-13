package auca.ac.rw.dto;

import jakarta.validation.constraints.Size;

public record SectorPatchRequest(
        @Size(max = 20) String code,
        @Size(max = 100) String name,
        @Size(max = 20) String districtCode
) {
}

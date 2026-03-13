package auca.ac.rw.dto;

import jakarta.validation.constraints.Size;

public record DistrictPatchRequest(
        @Size(max = 20) String code,
        @Size(max = 100) String name,
        @Size(max = 20) String provinceCode
) {
}

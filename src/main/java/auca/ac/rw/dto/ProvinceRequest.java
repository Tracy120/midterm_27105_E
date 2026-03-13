package auca.ac.rw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProvinceRequest(
        @NotBlank @Size(max = 20) String code,
        @NotBlank @Size(max = 100) String name
) {
}

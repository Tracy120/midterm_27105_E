package auca.ac.rw.dto;

import jakarta.validation.constraints.Size;

public record VillagePatchRequest(
        @Size(max = 30) String code,
        @Size(max = 120) String name,
        @Size(max = 20) String cellCode
) {
}

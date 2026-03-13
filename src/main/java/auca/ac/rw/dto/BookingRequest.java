package auca.ac.rw.dto;

import auca.ac.rw.entity.BookingStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequest(
        @NotBlank String bookingReference,
        @NotNull Long userId,
        @NotNull Long tourPackageId,
        @NotNull @FutureOrPresent LocalDate travelDate,
        @NotNull @Min(1) Integer numberOfPeople,
        BookingStatus status
) {
}

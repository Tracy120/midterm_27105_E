package auca.ac.rw.dto;

import auca.ac.rw.entity.BookingStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record BookingPatchRequest(
        String bookingReference,
        @Positive Long userId,
        @Positive Long tourPackageId,
        @FutureOrPresent LocalDate travelDate,
        @Min(1) Integer numberOfPeople,
        BookingStatus status
) {
}

package auca.ac.rw.dto;

import auca.ac.rw.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        String bookingReference,
        LocalDateTime bookingDate,
        LocalDate travelDate,
        Integer numberOfPeople,
        BookingStatus status,
        UserBookingSummary user,
        TourPackageSummary tourPackage
) {
}

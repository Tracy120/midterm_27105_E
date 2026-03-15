package auca.ac.rw.tourbook.controller;

import auca.ac.rw.tourbook.model.Booking;
import auca.ac.rw.tourbook.model.BookingStatus;
import auca.ac.rw.tourbook.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/booking/place/{userId}/{tourPackageId}")
    public ResponseEntity<String> placeBooking(@PathVariable("userId") Long userId,
                                               @PathVariable("tourPackageId") Long tourPackageId,
                                               @Valid @RequestBody Booking booking) {
        String result = bookingService.placeBooking(userId, tourPackageId, booking);
        return result.startsWith("Error:")
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(result)
                : ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/booking/all")
    public ResponseEntity<Page<Booking>> getAllBookings(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                                        @RequestParam(value = "sortBy", defaultValue = "bookingDate") String sortBy) {
        return ResponseEntity.ok(bookingService.getAllBookings(page, size, sortBy));
    }

    @GetMapping("/booking/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable("id") Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/booking/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable("userId") Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        if (bookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/booking/update/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable("id") Long id,
                                                 @Valid @RequestBody Booking booking,
                                                 @RequestParam(value = "userId", required = false) Long userId,
                                                 @RequestParam(value = "tourPackageId", required = false) Long tourPackageId) {
        Booking updatedBooking = bookingService.updateBooking(id, booking, userId, tourPackageId);
        if (updatedBooking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(updatedBooking);
    }

    @PatchMapping("/booking/status/{id}")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable("id") Long id,
                                                       @RequestParam("status") BookingStatus status) {
        Booking updatedBooking = bookingService.updateBookingStatus(id, status);
        return updatedBooking != null ? ResponseEntity.ok(updatedBooking) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/booking/delete/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable("id") Long id) {
        String result = bookingService.deleteBooking(id);
        return result.startsWith("Error:")
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(result)
                : ResponseEntity.ok(result);
    }
}

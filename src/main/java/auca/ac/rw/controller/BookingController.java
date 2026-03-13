package auca.ac.rw.controller;

import auca.ac.rw.dto.BookingPatchRequest;
import auca.ac.rw.dto.BookingRequest;
import auca.ac.rw.dto.BookingResponse;
import auca.ac.rw.dto.PageResponse;
import auca.ac.rw.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PutMapping("/{id}")
    public BookingResponse updateBooking(@PathVariable Long id, @Valid @RequestBody BookingRequest request) {
        return bookingService.updateBooking(id, request);
    }

    @PatchMapping("/{id}")
    public BookingResponse patchBooking(@PathVariable Long id, @Valid @RequestBody BookingPatchRequest request) {
        return bookingService.patchBooking(id, request);
    }

    @GetMapping("/{id}")
    public BookingResponse getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @GetMapping
    public PageResponse<BookingResponse> getBookings(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(defaultValue = "bookingDate") String sortBy,
                                                     @RequestParam(defaultValue = "desc") String direction) {
        return bookingService.getBookings(page, size, sortBy, direction);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}

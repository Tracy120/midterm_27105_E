package auca.ac.rw.service;

import auca.ac.rw.dto.BookingPatchRequest;
import auca.ac.rw.dto.BookingRequest;
import auca.ac.rw.dto.BookingResponse;
import auca.ac.rw.dto.PageResponse;
import auca.ac.rw.dto.TourPackageSummary;
import auca.ac.rw.dto.UserBookingSummary;
import auca.ac.rw.entity.Booking;
import auca.ac.rw.entity.BookingStatus;
import auca.ac.rw.repository.BookingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class BookingService {

    private static final Set<String> BOOKING_SORT_FIELDS = Set.of("bookingDate", "travelDate", "numberOfPeople", "status");

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final TourPackageService tourPackageService;

    public BookingService(BookingRepository bookingRepository,
                          UserService userService,
                          TourPackageService tourPackageService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.tourPackageService = tourPackageService;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Booking booking = new Booking();
        booking.setBookingDate(LocalDateTime.now());
        validateUniqueBookingReference(request.bookingReference().trim(), null);
        applyBookingChanges(booking, request);
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking booking = findBooking(id);
        validateUniqueBookingReference(request.bookingReference().trim(), booking.getId());
        applyBookingChanges(booking, request);
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse patchBooking(Long id, BookingPatchRequest request) {
        Booking booking = findBooking(id);
        String updatedBookingReference = resolveRequiredPatchValue(
                booking.getBookingReference(),
                request.bookingReference(),
                "bookingReference"
        );
        validateUniqueBookingReference(updatedBookingReference, booking.getId());
        applyBookingPatch(booking, request);
        return toResponse(bookingRepository.save(booking));
    }

    public BookingResponse getBooking(Long id) {
        return toResponse(findBooking(id));
    }

    public PageResponse<BookingResponse> getBookings(int page, int size, String sortBy, String direction) {
        String resolvedSortBy = resolveSortField(sortBy, BOOKING_SORT_FIELDS, "bookingDate");
        Sort.Direction sortDirection = resolveDirection(direction);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, resolvedSortBy));
        Page<Booking> bookingPage = bookingRepository.findAll(pageRequest);
        List<BookingResponse> bookings = new ArrayList<>();

        for (Booking booking : bookingPage.getContent()) {
            bookings.add(toResponse(booking));
        }

        return new PageResponse<>(
                bookings,
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages(),
                bookingPage.isFirst(),
                bookingPage.isLast(),
                resolvedSortBy,
                sortDirection.name()
        );
    }

    @Transactional
    public void deleteBooking(Long id) {
        bookingRepository.delete(findBooking(id));
    }

    public Booking findBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found."));
    }

    private BookingResponse toResponse(Booking booking) {
        UserBookingSummary userSummary = userService.toBookingSummary(booking.getUser());
        TourPackageSummary packageSummary = tourPackageService.toSummary(booking.getTourPackage());

        return new BookingResponse(
                booking.getId(),
                booking.getBookingReference(),
                booking.getBookingDate(),
                booking.getTravelDate(),
                booking.getNumberOfPeople(),
                booking.getStatus(),
                userSummary,
                packageSummary
        );
    }

    private void applyBookingChanges(Booking booking, BookingRequest request) {
        BookingStatus status = request.status();
        if (status == null) {
            status = BookingStatus.PENDING;
        }

        booking.setBookingReference(request.bookingReference().trim());
        booking.setTravelDate(request.travelDate());
        booking.setNumberOfPeople(request.numberOfPeople());
        booking.setStatus(status);
        booking.setUser(userService.findUser(request.userId()));
        booking.setTourPackage(tourPackageService.findPackage(request.tourPackageId()));
    }

    private void applyBookingPatch(Booking booking, BookingPatchRequest request) {
        if (request.bookingReference() != null) {
            booking.setBookingReference(normalizeRequiredValue(request.bookingReference(), "bookingReference"));
        }
        if (request.travelDate() != null) {
            booking.setTravelDate(request.travelDate());
        }
        if (request.numberOfPeople() != null) {
            booking.setNumberOfPeople(request.numberOfPeople());
        }
        if (request.status() != null) {
            booking.setStatus(request.status());
        }
        if (request.userId() != null) {
            booking.setUser(userService.findUser(request.userId()));
        }
        if (request.tourPackageId() != null) {
            booking.setTourPackage(tourPackageService.findPackage(request.tourPackageId()));
        }
    }

    private void validateUniqueBookingReference(String bookingReference, Long currentBookingId) {
        if (bookingRepository.existsByBookingReferenceIgnoreCase(bookingReference)) {
            Booking existingBooking = bookingRepository.findByBookingReferenceIgnoreCase(bookingReference);
            if (existingBooking != null && !existingBooking.getId().equals(currentBookingId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking reference already exists.");
            }
        }
    }

    private String resolveRequiredPatchValue(String currentValue, String patchedValue, String fieldName) {
        if (patchedValue == null) {
            return currentValue;
        }
        return normalizeRequiredValue(patchedValue, fieldName);
    }

    private String normalizeRequiredValue(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must not be blank.");
        }
        return value.trim();
    }

    private String resolveSortField(String sortBy, Set<String> allowedFields, String defaultField) {
        if (!StringUtils.hasText(sortBy)) {
            return defaultField;
        }
        if (!allowedFields.contains(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported sort field: " + sortBy);
        }
        return sortBy;
    }

    private Sort.Direction resolveDirection(String direction) {
        if (!StringUtils.hasText(direction)) {
            return Sort.Direction.ASC;
        }
        try {
            return Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported sort direction: " + direction);
        }
    }
}

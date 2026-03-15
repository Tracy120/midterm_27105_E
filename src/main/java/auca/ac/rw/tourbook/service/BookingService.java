package auca.ac.rw.tourbook.service;

import auca.ac.rw.tourbook.model.Booking;
import auca.ac.rw.tourbook.model.BookingStatus;
import auca.ac.rw.tourbook.model.TourPackage;
import auca.ac.rw.tourbook.model.User;
import auca.ac.rw.tourbook.repository.BookingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

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

    public String placeBooking(Long userId, Long tourPackageId, Booking booking) {
        if (booking == null) {
            return "Error: Booking payload is required.";
        }
        if (!StringUtils.hasText(booking.getBookingReference())) {
            return "Error: Booking reference is required.";
        }
        if (bookingRepository.existsByBookingReferenceIgnoreCase(booking.getBookingReference().trim())) {
            return "Error: Booking reference already exists.";
        }

        User user = userService.findUser(userId);
        if (user == null) {
            return "Error: User not found.";
        }

        TourPackage tourPackage = tourPackageService.findPackage(tourPackageId);
        if (tourPackage == null) {
            return "Error: Tour package not found.";
        }

        booking.setBookingReference(booking.getBookingReference().trim());
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(booking.getStatus() == null ? BookingStatus.PENDING : booking.getStatus());
        booking.setUser(user);
        booking.setTourPackage(tourPackage);
        bookingRepository.save(booking);
        return "Success: Booking placed successfully.";
    }

    public Page<Booking> getAllBookings(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return bookingRepository.findAll(pageable);
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Booking updateBooking(Long id, Booking newBookingData, Long userId, Long tourPackageId) {
        Optional<Booking> existingBookingOpt = bookingRepository.findById(id);
        if (!existingBookingOpt.isPresent() || newBookingData == null) {
            return null;
        }

        Booking existingBooking = existingBookingOpt.get();
        if (StringUtils.hasText(newBookingData.getBookingReference())) {
            existingBooking.setBookingReference(newBookingData.getBookingReference().trim());
        }
        if (newBookingData.getTravelDate() != null) {
            existingBooking.setTravelDate(newBookingData.getTravelDate());
        }
        if (newBookingData.getNumberOfPeople() != null) {
            existingBooking.setNumberOfPeople(newBookingData.getNumberOfPeople());
        }
        if (newBookingData.getStatus() != null) {
            existingBooking.setStatus(newBookingData.getStatus());
        }

        if (userId != null) {
            User user = userService.findUser(userId);
            if (user == null) {
                return null;
            }
            existingBooking.setUser(user);
        }

        if (tourPackageId != null) {
            TourPackage tourPackage = tourPackageService.findPackage(tourPackageId);
            if (tourPackage == null) {
                return null;
            }
            existingBooking.setTourPackage(tourPackage);
        }

        return bookingRepository.save(existingBooking);
    }

    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (!bookingOpt.isPresent()) {
            return null;
        }

        Booking booking = bookingOpt.get();
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public String deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            return "Error: Booking not found.";
        }
        bookingRepository.deleteById(id);
        return "Success: Booking deleted.";
    }
}

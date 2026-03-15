package auca.ac.rw.tourbook.repository;

import auca.ac.rw.tourbook.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBookingReferenceIgnoreCase(String bookingReference);

    Booking findByBookingReferenceIgnoreCase(String bookingReference);

    List<Booking> findByUserId(Long userId);
}

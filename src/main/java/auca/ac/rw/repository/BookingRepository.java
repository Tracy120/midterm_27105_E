package auca.ac.rw.repository;

import auca.ac.rw.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBookingReferenceIgnoreCase(String bookingReference);

    Booking findByBookingReferenceIgnoreCase(String bookingReference);
}

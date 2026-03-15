package auca.ac.rw.tourbook.repository;

import auca.ac.rw.tourbook.model.TourGuide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourGuideRepository extends JpaRepository<TourGuide, Long> {
    boolean existsByGuideCodeIgnoreCase(String guideCode);

    boolean existsByEmailIgnoreCase(String email);

    TourGuide findByGuideCodeIgnoreCase(String guideCode);

    TourGuide findByEmailIgnoreCase(String email);
}

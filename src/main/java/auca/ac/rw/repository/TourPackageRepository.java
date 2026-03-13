package auca.ac.rw.repository;

import auca.ac.rw.entity.TourPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourPackageRepository extends JpaRepository<TourPackage, Long> {
    boolean existsByPackageCodeIgnoreCase(String packageCode);

    boolean existsByTitleIgnoreCase(String title);

    TourPackage findByPackageCodeIgnoreCase(String packageCode);

    TourPackage findByTitleIgnoreCase(String title);
}

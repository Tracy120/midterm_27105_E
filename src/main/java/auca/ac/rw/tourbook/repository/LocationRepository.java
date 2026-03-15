package auca.ac.rw.tourbook.repository;

import auca.ac.rw.tourbook.model.Location;
import auca.ac.rw.tourbook.model.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByCodeIgnoreCase(String code);

    Optional<Location> findByCodeIgnoreCase(String code);

    Optional<Location> findByCodeIgnoreCaseAndType(String code, LocationType type);

    List<Location> findByNameIgnoreCaseAndType(String name, LocationType type);

    List<Location> findByTypeOrderByNameAsc(LocationType type);
}

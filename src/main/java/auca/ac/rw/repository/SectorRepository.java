package auca.ac.rw.repository;

import auca.ac.rw.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    boolean existsByCodeIgnoreCase(String code);

    Optional<Sector> findByCodeIgnoreCase(String code);

    List<Sector> findByDistrict_CodeIgnoreCaseOrderByNameAsc(String districtCode);
}

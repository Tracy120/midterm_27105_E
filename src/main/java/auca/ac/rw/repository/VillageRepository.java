package auca.ac.rw.repository;

import auca.ac.rw.entity.Village;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VillageRepository extends JpaRepository<Village, Long> {
    boolean existsByCodeIgnoreCase(String code);

    Optional<Village> findByCodeIgnoreCase(String code);

    List<Village> findByNameIgnoreCase(String name);

    List<Village> findByCell_CodeIgnoreCaseOrderByNameAsc(String cellCode);
}

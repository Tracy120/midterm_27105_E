package auca.ac.rw.repository;

import auca.ac.rw.entity.Cell;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CellRepository extends JpaRepository<Cell, Long> {
    boolean existsByCodeIgnoreCase(String code);

    Optional<Cell> findByCodeIgnoreCase(String code);

    List<Cell> findBySector_CodeIgnoreCaseOrderByNameAsc(String sectorCode);
}

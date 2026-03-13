package auca.ac.rw.repository;

import auca.ac.rw.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    boolean existsByCodeIgnoreCase(String code);

    Optional<District> findByCodeIgnoreCase(String code);

    List<District> findByProvince_CodeIgnoreCaseOrderByNameAsc(String provinceCode);
}

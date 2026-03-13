package auca.ac.rw.repository;

import auca.ac.rw.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    boolean existsByCodeIgnoreCase(String code);

    Optional<Province> findByCodeIgnoreCase(String code);
}

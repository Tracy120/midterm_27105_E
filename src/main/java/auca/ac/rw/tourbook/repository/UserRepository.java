package auca.ac.rw.tourbook.repository;

import auca.ac.rw.tourbook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUserCodeIgnoreCase(String userCode);

    User findByEmailIgnoreCase(String email);

    User findByUserCodeIgnoreCase(String userCode);

    @Query("SELECT u FROM User u WHERE LOWER(u.location.parent.parent.parent.parent.name) = LOWER(:provinceName)")
    List<User> findUsersByProvinceName(@Param("provinceName") String provinceName);

    @Query("SELECT u FROM User u WHERE LOWER(u.location.parent.parent.parent.parent.code) = LOWER(:provinceCode)")
    List<User> findUsersByProvinceCode(@Param("provinceCode") String provinceCode);
}

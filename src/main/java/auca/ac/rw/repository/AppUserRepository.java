package auca.ac.rw.repository;

import auca.ac.rw.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUserCodeIgnoreCase(String userCode);

    AppUser findByEmailIgnoreCase(String email);

    AppUser findByUserCodeIgnoreCase(String userCode);

    @Query("select u from AppUser u "
            + "join u.village v "
            + "join v.cell c "
            + "join c.sector s "
            + "join s.district d "
            + "join d.province p "
            + "where lower(p.code) = lower(:provinceCode) "
            + "order by u.firstName asc, u.lastName asc")
    List<AppUser> findAllByProvinceCode(@Param("provinceCode") String provinceCode);

    @Query("select u from AppUser u "
            + "join u.village v "
            + "join v.cell c "
            + "join c.sector s "
            + "join s.district d "
            + "join d.province p "
            + "where lower(p.name) = lower(:provinceName) "
            + "order by u.firstName asc, u.lastName asc")
    List<AppUser> findAllByProvinceName(@Param("provinceName") String provinceName);

    @Query("select u from AppUser u "
            + "join u.village v "
            + "join v.cell c "
            + "join c.sector s "
            + "join s.district d "
            + "join d.province p "
            + "where (:provinceCode is null or lower(p.code) = lower(:provinceCode)) "
            + "and (:districtCode is null or lower(d.code) = lower(:districtCode)) "
            + "and (:sectorCode is null or lower(s.code) = lower(:sectorCode)) "
            + "and (:cellCode is null or lower(c.code) = lower(:cellCode)) "
            + "and (:villageCode is null or lower(v.code) = lower(:villageCode)) "
            + "order by u.firstName asc, u.lastName asc")
    List<AppUser> findAllByLocation(
            @Param("provinceCode") String provinceCode,
            @Param("districtCode") String districtCode,
            @Param("sectorCode") String sectorCode,
            @Param("cellCode") String cellCode,
            @Param("villageCode") String villageCode
    );
}

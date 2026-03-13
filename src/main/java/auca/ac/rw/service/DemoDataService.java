package auca.ac.rw.service;

import auca.ac.rw.dto.CellRequest;
import auca.ac.rw.dto.DemoResetResponse;
import auca.ac.rw.dto.DemoSeedResponse;
import auca.ac.rw.dto.DistrictRequest;
import auca.ac.rw.dto.ProvinceRequest;
import auca.ac.rw.dto.SectorRequest;
import auca.ac.rw.dto.TourGuideRequest;
import auca.ac.rw.dto.TourGuideResponse;
import auca.ac.rw.dto.TourPackageRequest;
import auca.ac.rw.dto.TourPackageResponse;
import auca.ac.rw.dto.UserProfileRequest;
import auca.ac.rw.dto.UserRequest;
import auca.ac.rw.dto.UserResponse;
import auca.ac.rw.dto.VillageRequest;
import auca.ac.rw.entity.Booking;
import auca.ac.rw.entity.Cell;
import auca.ac.rw.entity.District;
import auca.ac.rw.entity.Province;
import auca.ac.rw.entity.Sector;
import auca.ac.rw.entity.TourGuide;
import auca.ac.rw.entity.TourPackage;
import auca.ac.rw.entity.AppUser;
import auca.ac.rw.entity.Village;
import auca.ac.rw.repository.AppUserRepository;
import auca.ac.rw.repository.BookingRepository;
import auca.ac.rw.repository.CellRepository;
import auca.ac.rw.repository.DistrictRepository;
import auca.ac.rw.repository.ProvinceRepository;
import auca.ac.rw.repository.SectorRepository;
import auca.ac.rw.repository.TourGuideRepository;
import auca.ac.rw.repository.TourPackageRepository;
import auca.ac.rw.repository.UserProfileRepository;
import auca.ac.rw.repository.VillageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class DemoDataService {

    private final BookingRepository bookingRepository;
    private final TourPackageRepository tourPackageRepository;
    private final TourGuideRepository tourGuideRepository;
    private final UserProfileRepository userProfileRepository;
    private final AppUserRepository appUserRepository;
    private final VillageRepository villageRepository;
    private final CellRepository cellRepository;
    private final SectorRepository sectorRepository;
    private final DistrictRepository districtRepository;
    private final ProvinceRepository provinceRepository;
    private final BookingService bookingService;
    private final TourPackageService tourPackageService;
    private final TourGuideService tourGuideService;
    private final UserService userService;
    private final LocationService locationService;

    public DemoDataService(BookingRepository bookingRepository,
                           TourPackageRepository tourPackageRepository,
                           TourGuideRepository tourGuideRepository,
                           UserProfileRepository userProfileRepository,
                           AppUserRepository appUserRepository,
                           VillageRepository villageRepository,
                           CellRepository cellRepository,
                           SectorRepository sectorRepository,
                           DistrictRepository districtRepository,
                           ProvinceRepository provinceRepository,
                           BookingService bookingService,
                           TourPackageService tourPackageService,
                           TourGuideService tourGuideService,
                           UserService userService,
                           LocationService locationService) {
        this.bookingRepository = bookingRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.tourGuideRepository = tourGuideRepository;
        this.userProfileRepository = userProfileRepository;
        this.appUserRepository = appUserRepository;
        this.villageRepository = villageRepository;
        this.cellRepository = cellRepository;
        this.sectorRepository = sectorRepository;
        this.districtRepository = districtRepository;
        this.provinceRepository = provinceRepository;
        this.bookingService = bookingService;
        this.tourPackageService = tourPackageService;
        this.tourGuideService = tourGuideService;
        this.userService = userService;
        this.locationService = locationService;
    }

    @Transactional
    public DemoResetResponse reset() {
        long deletedBookings = bookingRepository.count();
        long deletedPackages = tourPackageRepository.count();
        long deletedGuides = tourGuideRepository.count();
        long deletedProfiles = userProfileRepository.count();
        long deletedUsers = appUserRepository.count();
        long deletedVillages = villageRepository.count();
        long deletedCells = cellRepository.count();
        long deletedSectors = sectorRepository.count();
        long deletedDistricts = districtRepository.count();
        long deletedProvinces = provinceRepository.count();

        for (Booking booking : bookingRepository.findAll()) {
            bookingService.deleteBooking(booking.getId());
        }
        for (TourPackage tourPackage : tourPackageRepository.findAll()) {
            tourPackageService.deletePackage(tourPackage.getId());
        }
        for (TourGuide guide : tourGuideRepository.findAll()) {
            tourGuideService.deleteGuide(guide.getId());
        }

        userProfileRepository.deleteAll();

        for (AppUser user : appUserRepository.findAll()) {
            userService.deleteUser(user.getId());
        }
        for (Village village : villageRepository.findAll()) {
            locationService.deleteVillage(village.getId());
        }
        for (Cell cell : cellRepository.findAll()) {
            locationService.deleteCell(cell.getId());
        }
        for (Sector sector : sectorRepository.findAll()) {
            locationService.deleteSector(sector.getId());
        }
        for (District district : districtRepository.findAll()) {
            locationService.deleteDistrict(district.getId());
        }
        for (Province province : provinceRepository.findAll()) {
            locationService.deleteProvince(province.getId());
        }

        return new DemoResetResponse(
                "Demo data cleared successfully.",
                deletedBookings,
                deletedPackages,
                deletedGuides,
                deletedProfiles,
                deletedUsers,
                deletedVillages,
                deletedCells,
                deletedSectors,
                deletedDistricts,
                deletedProvinces
        );
    }

    @Transactional
    public DemoSeedResponse resetAndSeed() {
        reset();

        var province = locationService.createProvince(new ProvinceRequest("PRV-901", "Northern Province Demo"));
        var district = locationService.createDistrict(new DistrictRequest("DST-901", "Gicumbi Demo", "PRV-901"));
        var sector = locationService.createSector(new SectorRequest("SEC-901", "Byumba Demo", "DST-901"));
        var cell = locationService.createCell(new CellRequest("CEL-901", "Ngondore Demo", "SEC-901"));
        var village = locationService.createVillage(new VillageRequest("VLG-901", "Kabeza Demo", "CEL-901"));

        TourGuideResponse guideOne = tourGuideService.createGuide(new TourGuideRequest(
                "GDE-901",
                "Brenda Uwera",
                "brenda.uwera@example.com",
                "0788000901",
                "Bird watching"
        ));
        TourGuideResponse guideTwo = tourGuideService.createGuide(new TourGuideRequest(
                "GDE-902",
                "Kevin Mutoni",
                "kevin.mutoni@example.com",
                "0788000902",
                "Cultural tourism"
        ));
        TourGuideResponse guideThree = tourGuideService.createGuide(new TourGuideRequest(
                "GDE-903",
                "Sonia Ingabire",
                "sonia.ingabire@example.com",
                "0788000903",
                "Hiking"
        ));

        UserResponse userOne = userService.createUser(new UserRequest(
                "USR-901",
                "Lionel",
                "Hategekimana",
                "lionel901@example.com",
                "0788000911",
                "VLG-901",
                null,
                new UserProfileRequest("1199988877769901", "MALE", "0788777901", "P9901")
        ));
        UserResponse userTwo = userService.createUser(new UserRequest(
                "USR-902",
                "Ariane",
                "Uwimana",
                "ariane902@example.com",
                "0788000912",
                "VLG-901",
                null,
                new UserProfileRequest("1199988877769902", "FEMALE", "0788777902", "P9902")
        ));
        UserResponse userThree = userService.createUser(new UserRequest(
                "USR-903",
                "David",
                "Nshimiyimana",
                "david903@example.com",
                "0788000913",
                "VLG-901",
                null,
                new UserProfileRequest("1199988877769903", "MALE", "0788777903", "P9903")
        ));

        TourPackageResponse packageOne = tourPackageService.createPackage(new TourPackageRequest(
                "PKG-901",
                "Volcano Ridge Trek",
                "Two-day mountain trek with sunrise views.",
                BigDecimal.valueOf(540),
                2,
                Set.of(guideOne.id())
        ));
        TourPackageResponse packageTwo = tourPackageService.createPackage(new TourPackageRequest(
                "PKG-902",
                "Kigali Heritage Walk",
                "Museum, city market, and food experience.",
                BigDecimal.valueOf(120),
                1,
                Set.of(guideTwo.id())
        ));
        TourPackageResponse packageThree = tourPackageService.createPackage(new TourPackageRequest(
                "PKG-903",
                "Lake Kivu Weekend",
                "Boat ride and lakeside rest.",
                BigDecimal.valueOf(320),
                2,
                Set.of(guideThree.id())
        ));

        return new DemoSeedResponse(
                "Demo data reset and seeded successfully. Use these IDs in Postman.",
                province.id(),
                district.id(),
                sector.id(),
                cell.id(),
                village.id(),
                guideOne.id(),
                guideTwo.id(),
                guideThree.id(),
                userOne.id(),
                userTwo.id(),
                userThree.id(),
                packageOne.id(),
                packageTwo.id(),
                packageThree.id()
        );
    }
}

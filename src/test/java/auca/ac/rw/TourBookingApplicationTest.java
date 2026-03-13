package auca.ac.rw;

import auca.ac.rw.dto.BookingPatchRequest;
import auca.ac.rw.dto.BookingRequest;
import auca.ac.rw.dto.CellPatchRequest;
import auca.ac.rw.dto.CellRequest;
import auca.ac.rw.dto.DistrictPatchRequest;
import auca.ac.rw.dto.DistrictRequest;
import auca.ac.rw.dto.ProvincePatchRequest;
import auca.ac.rw.dto.ProvinceRequest;
import auca.ac.rw.dto.SectorPatchRequest;
import auca.ac.rw.dto.SectorRequest;
import auca.ac.rw.dto.TourGuidePatchRequest;
import auca.ac.rw.dto.TourGuideRequest;
import auca.ac.rw.dto.TourPackagePatchRequest;
import auca.ac.rw.dto.TourPackageRequest;
import auca.ac.rw.dto.UserPatchRequest;
import auca.ac.rw.dto.UserProfilePatchRequest;
import auca.ac.rw.dto.UserProfileRequest;
import auca.ac.rw.dto.UserRequest;
import auca.ac.rw.dto.VillagePatchRequest;
import auca.ac.rw.dto.VillageRequest;
import auca.ac.rw.entity.BookingStatus;
import auca.ac.rw.repository.BookingRepository;
import auca.ac.rw.repository.CellRepository;
import auca.ac.rw.repository.DistrictRepository;
import auca.ac.rw.repository.ProvinceRepository;
import auca.ac.rw.repository.SectorRepository;
import auca.ac.rw.repository.TourGuideRepository;
import auca.ac.rw.repository.TourPackageRepository;
import auca.ac.rw.repository.UserProfileRepository;
import auca.ac.rw.repository.VillageRepository;
import auca.ac.rw.repository.AppUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TourBookingApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourPackageRepository tourPackageRepository;

    @Autowired
    private TourGuideRepository tourGuideRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private VillageRepository villageRepository;

    @Autowired
    private CellRepository cellRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @BeforeEach
    void cleanDatabase() {
        bookingRepository.deleteAll();
        tourPackageRepository.deleteAll();
        tourGuideRepository.deleteAll();
        userProfileRepository.deleteAll();
        appUserRepository.deleteAll();
        villageRepository.deleteAll();
        cellRepository.deleteAll();
        sectorRepository.deleteAll();
        districtRepository.deleteAll();
        provinceRepository.deleteAll();
    }

    @Test
    void shouldCreateUserThroughVillageAndRetrieveByProvinceCode() throws Exception {
        createLocationHierarchy();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(
                                "USR-001",
                                "Aline",
                                "Uwase",
                                "aline@example.com",
                                "0788000001",
                                "VLG-001",
                                null,
                                new UserProfileRequest("1199988877766655", "FEMALE", "0788776655", "P12345")
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.location.villageCode").value("VLG-001"))
                .andExpect(jsonPath("$.location.provinceCode").value("PRV-N"));

        mockMvc.perform(get("/api/users/by-province").param("provinceCode", "PRV-N"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("aline@example.com"))
                .andExpect(jsonPath("$[0].location.provinceName").value("Northern Province"));
    }

    @Test
    void shouldRetrieveUsersByCellCode() throws Exception {
        createLocationHierarchyWithIds(
                "PRV-N1",
                "Gicumbi One",
                "DST-N1",
                "Byumba One",
                "SEC-N1",
                "Ngondore One",
                "CEL-N1",
                "Kabeza One",
                "VLG-N1"
        );
        createLocationHierarchyWithIds(
                "PRV-S1",
                "Huye One",
                "DST-S1",
                "Ngoma One",
                "SEC-S1",
                "Matyazo One",
                "CEL-S1",
                "Butare One",
                "VLG-S1"
        );

        createUserAtVillage("USR-CELL-1", "claire.cell1@example.com", "VLG-N1");
        createUserAtVillage("USR-CELL-2", "sam.cell2@example.com", "VLG-S1");

        mockMvc.perform(get("/api/users/by-location").param("cellCode", "CEL-N1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("claire.cell1@example.com"))
                .andExpect(jsonPath("$[0].location.cellCode").value("CEL-N1"));

        mockMvc.perform(get("/api/users/by-location").param("districtCode", "DST-S1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("sam.cell2@example.com"))
                .andExpect(jsonPath("$[0].location.districtCode").value("DST-S1"));
    }

    @Test
    void shouldRejectDuplicateUserEmail() throws Exception {
        createLocationHierarchy();

        UserRequest request = new UserRequest(
                "USR-001",
                "Aline",
                "Uwase",
                "aline@example.com",
                "0788000001",
                "VLG-001",
                null,
                null
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(
                                "USR-002",
                                "Alice",
                                "Murekatete",
                                "aline@example.com",
                                "0788000002",
                                "VLG-001",
                                null,
                                null
                        ))))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldPaginateAndSortPackagesByPriceDescending() throws Exception {
        long guideId = createGuide("GDE-001", "Eric Guide", "guide@example.com");

        createPackage("PKG-001", "Akagera Day Trip", BigDecimal.valueOf(120), 1, guideId);
        createPackage("PKG-002", "Luxury Gorilla Trek", BigDecimal.valueOf(900), 3, guideId);
        createPackage("PKG-003", "Lake Kivu Escape", BigDecimal.valueOf(350), 2, guideId);

        mockMvc.perform(get("/api/packages")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortBy", "price")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[0].title").value("Luxury Gorilla Trek"))
                .andExpect(jsonPath("$.content[1].title").value("Lake Kivu Escape"));
    }

    @Test
    void shouldCreateBookingLinkedToUserAndPackage() throws Exception {
        createLocationHierarchy();
        long userId = createUser("booker@example.com");
        long guideId = createGuide("GDE-001", "Eric Guide", "guide@example.com");
        long packageId = createPackage("PKG-010", "Nyungwe Adventure", BigDecimal.valueOf(500), 2, guideId);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookingRequest(
                                "BKG-001",
                                userId,
                                packageId,
                                LocalDate.now().plusDays(5),
                                2,
                                BookingStatus.CONFIRMED
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("booker@example.com"))
                .andExpect(jsonPath("$.tourPackage.title").value("Nyungwe Adventure"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void shouldSupportPutPatchAndDeleteForUsers() throws Exception {
        createLocationHierarchy();
        long userId = createUser("crud.user@example.com");

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(
                                "USR-CRUD",
                                "Patrick",
                                "Ndayisaba",
                                "crud.user@example.com",
                                "0788333301",
                                "VLG-001",
                                null,
                                new UserProfileRequest("1199988877767001", "MALE", "0788444401", "P7001")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Patrick"))
                .andExpect(jsonPath("$.profile.passportNumber").value("P7001"));

        mockMvc.perform(patch("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserPatchRequest(
                                null,
                                null,
                                null,
                                "patrick.updated@example.com",
                                "0788333399",
                                null,
                                null,
                                new UserProfilePatchRequest(null, null, "0788444499", null)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("patrick.updated@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("0788333399"))
                .andExpect(jsonPath("$.profile.emergencyContact").value("0788444499"));

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSupportPutPatchAndDeleteForGuides() throws Exception {
        long guideId = createGuide("GDE-101", "Alice Guide", "alice.guide@example.com");
        long packageId = createPackage("PKG-101", "Akagera Classic", BigDecimal.valueOf(250), 2, guideId);

        mockMvc.perform(put("/api/guides/{id}", guideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TourGuideRequest(
                                "GDE-101",
                                "Alice Guide",
                                "alice.guide@example.com",
                                "0788555501",
                                "Wildlife"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("0788555501"));

        mockMvc.perform(patch("/api/guides/{id}", guideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TourGuidePatchRequest(
                                null,
                                null,
                                null,
                                null,
                                "Bird watching"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialization").value("Bird watching"));

        mockMvc.perform(delete("/api/guides/{id}", guideId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/guides/{id}", guideId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/packages/{id}", packageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guides").isEmpty());
    }

    @Test
    void shouldSupportPutPatchAndDeleteForPackages() throws Exception {
        long guideIdOne = createGuide("GDE-201", "Claude Guide", "claude.guide@example.com");
        long guideIdTwo = createGuide("GDE-202", "Diane Guide", "diane.guide@example.com");
        long packageId = createPackage("PKG-201", "Nyungwe Trail", BigDecimal.valueOf(420), 2, guideIdOne);

        mockMvc.perform(put("/api/packages/{id}", packageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TourPackageRequest(
                                "PKG-201",
                                "Nyungwe Trail",
                                "Full forest experience with canopy walk.",
                                BigDecimal.valueOf(450),
                                3,
                                Set.of(guideIdOne, guideIdTwo)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(450))
                .andExpect(jsonPath("$.guides.length()").value(2));

        mockMvc.perform(patch("/api/packages/{id}", packageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TourPackagePatchRequest(
                                null,
                                null,
                                "Updated forest package with tea plantation stop.",
                                BigDecimal.valueOf(480),
                                null,
                                Set.of(guideIdTwo)
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated forest package with tea plantation stop."))
                .andExpect(jsonPath("$.price").value(480))
                .andExpect(jsonPath("$.guides.length()").value(1))
                .andExpect(jsonPath("$.guides[0].email").value("diane.guide@example.com"));

        mockMvc.perform(delete("/api/packages/{id}", packageId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/packages/{id}", packageId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSupportPutPatchAndDeleteForBookings() throws Exception {
        createLocationHierarchy();
        long userId = createUser("booking.crud@example.com");
        long guideId = createGuide("GDE-301", "Eric Guide", "eric.guide@example.com");
        long packageId = createPackage("PKG-301", "Kivu Retreat", BigDecimal.valueOf(390), 2, guideId);

        MvcResult bookingResult = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookingRequest(
                                "BKG-301",
                                userId,
                                packageId,
                                LocalDate.now().plusDays(4),
                                2,
                                BookingStatus.PENDING
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        long bookingId = extractId(bookingResult);

        mockMvc.perform(put("/api/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookingRequest(
                                "BKG-301",
                                userId,
                                packageId,
                                LocalDate.now().plusDays(6),
                                3,
                                BookingStatus.CONFIRMED
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfPeople").value(3))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        mockMvc.perform(patch("/api/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookingPatchRequest(
                                null,
                                null,
                                null,
                                LocalDate.now().plusDays(8),
                                5,
                                BookingStatus.CANCELLED
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfPeople").value(5))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(delete("/api/bookings/{id}", bookingId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/bookings/{id}", bookingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSupportFullCrudForLocationHierarchy() throws Exception {
        LocationIds ids = createLocationHierarchyWithIds(
                "PRV-FC",
                "District Full Crud",
                "DST-FC",
                "Sector Full Crud",
                "SEC-FC",
                "Cell Full Crud",
                "CEL-FC",
                "Village Full Crud",
                "VLG-FC"
        );

        mockMvc.perform(get("/api/locations/provinces/{id}", ids.provinceId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PRV-FC"));

        mockMvc.perform(get("/api/locations/districts").param("provinceCode", "PRV-FC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("DST-FC"));

        mockMvc.perform(get("/api/locations/sectors").param("districtCode", "DST-FC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("SEC-FC"));

        mockMvc.perform(get("/api/locations/cells").param("sectorCode", "SEC-FC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("CEL-FC"));

        mockMvc.perform(get("/api/locations/villages").param("cellCode", "CEL-FC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("VLG-FC"));

        mockMvc.perform(put("/api/locations/provinces/{id}", ids.provinceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProvinceRequest("PRV-FC-UPD", "Province Updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PRV-FC-UPD"));

        mockMvc.perform(put("/api/locations/districts/{id}", ids.districtId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DistrictRequest(
                                "DST-FC-UPD",
                                "District Updated",
                                "PRV-FC-UPD"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DST-FC-UPD"));

        mockMvc.perform(put("/api/locations/sectors/{id}", ids.sectorId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SectorRequest(
                                "SEC-FC-UPD",
                                "Sector Updated",
                                "DST-FC-UPD"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SEC-FC-UPD"));

        mockMvc.perform(put("/api/locations/cells/{id}", ids.cellId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CellRequest(
                                "CEL-FC-UPD",
                                "Cell Updated",
                                "SEC-FC-UPD"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CEL-FC-UPD"));

        mockMvc.perform(put("/api/locations/villages/{id}", ids.villageId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VillageRequest(
                                "VLG-FC-UPD",
                                "Village Updated",
                                "CEL-FC-UPD"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("VLG-FC-UPD"));

        mockMvc.perform(patch("/api/locations/provinces/{id}", ids.provinceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProvincePatchRequest(null, "Province Patched"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Province Patched"));

        mockMvc.perform(patch("/api/locations/districts/{id}", ids.districtId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DistrictPatchRequest(null, "District Patched", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("District Patched"));

        mockMvc.perform(patch("/api/locations/sectors/{id}", ids.sectorId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SectorPatchRequest(null, "Sector Patched", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sector Patched"));

        mockMvc.perform(patch("/api/locations/cells/{id}", ids.cellId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CellPatchRequest(null, "Cell Patched", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cell Patched"));

        mockMvc.perform(patch("/api/locations/villages/{id}", ids.villageId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VillagePatchRequest(null, "Village Patched", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Village Patched"));

        mockMvc.perform(delete("/api/locations/villages/{id}", ids.villageId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/locations/villages/{id}", ids.villageId()))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/locations/cells/{id}", ids.cellId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/locations/cells/{id}", ids.cellId()))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/locations/sectors/{id}", ids.sectorId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/locations/sectors/{id}", ids.sectorId()))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/locations/districts/{id}", ids.districtId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/locations/districts/{id}", ids.districtId()))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/locations/provinces/{id}", ids.provinceId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/locations/provinces/{id}", ids.provinceId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldResetAndSeedDemoData() throws Exception {
        MvcResult seedResult = mockMvc.perform(post("/api/demo/reset-and-seed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provinceId").exists())
                .andExpect(jsonPath("$.guide1Id").exists())
                .andExpect(jsonPath("$.user1Id").exists())
                .andExpect(jsonPath("$.package1Id").exists())
                .andReturn();

        JsonNode seedJson = objectMapper.readTree(seedResult.getResponse().getContentAsString());
        long userId = seedJson.get("user1Id").asLong();
        long packageId = seedJson.get("package1Id").asLong();

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userCode").value("USR-901"))
                .andExpect(jsonPath("$.location.villageCode").value("VLG-901"));

        mockMvc.perform(get("/api/packages/{id}", packageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packageCode").value("PKG-901"));

        mockMvc.perform(post("/api/demo/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deletedUsers").value(3))
                .andExpect(jsonPath("$.deletedPackages").value(3))
                .andExpect(jsonPath("$.deletedGuides").value(3));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    private void createLocationHierarchy() throws Exception {
        mockMvc.perform(post("/api/locations/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProvinceRequest("PRV-N", "Northern Province"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/locations/districts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DistrictRequest("DST-GIC", "Gicumbi", "PRV-N"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/locations/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SectorRequest("SEC-BYA", "Byumba", "DST-GIC"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/locations/cells")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CellRequest("CEL-NGO", "Ngondore", "SEC-BYA"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/locations/villages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VillageRequest("VLG-001", "Kabeza", "CEL-NGO"))))
                .andExpect(status().isCreated());
    }

    private LocationIds createLocationHierarchyWithIds(String provinceCode,
                                                       String districtName,
                                                       String districtCode,
                                                       String sectorName,
                                                       String sectorCode,
                                                       String cellName,
                                                       String cellCode,
                                                       String villageName,
                                                       String villageCode) throws Exception {
        MvcResult provinceResult = mockMvc.perform(post("/api/locations/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProvinceRequest(provinceCode, "Province " + provinceCode))))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult districtResult = mockMvc.perform(post("/api/locations/districts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DistrictRequest(districtCode, districtName, provinceCode))))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult sectorResult = mockMvc.perform(post("/api/locations/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SectorRequest(sectorCode, sectorName, districtCode))))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult cellResult = mockMvc.perform(post("/api/locations/cells")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CellRequest(cellCode, cellName, sectorCode))))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult villageResult = mockMvc.perform(post("/api/locations/villages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VillageRequest(villageCode, villageName, cellCode))))
                .andExpect(status().isCreated())
                .andReturn();

        return new LocationIds(
                extractId(provinceResult),
                extractId(districtResult),
                extractId(sectorResult),
                extractId(cellResult),
                extractId(villageResult)
        );
    }

    private long createGuide(String guideCode, String fullName, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/guides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TourGuideRequest(
                                guideCode,
                                fullName,
                                email,
                                "0788111111",
                                "Wildlife"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        return extractId(result);
    }

    private long createPackage(String packageCode, String title, BigDecimal price, int durationInDays, long guideId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TourPackageRequest(
                                packageCode,
                                title,
                                "Tour package for " + title,
                                price,
                                durationInDays,
                                Set.of(guideId)
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        return extractId(result);
    }

    private long createUser(String email) throws Exception {
        return createUserAtVillage("USR-BOOK", email, "VLG-001");
    }

    private long createUserAtVillage(String userCode, String email, String villageCode) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest(
                                userCode,
                                "Jean",
                                "Habimana",
                                email,
                                "0788222222",
                                villageCode,
                                null,
                                null
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        return extractId(result);
    }

    private long extractId(MvcResult result) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("id").asLong();
    }

    // Stores hierarchy IDs so the CRUD assertions can target each location level.
    private record LocationIds(long provinceId, long districtId, long sectorId, long cellId, long villageId) {
    }
}

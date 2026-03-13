package auca.ac.rw.service;

import auca.ac.rw.dto.CellPatchRequest;
import auca.ac.rw.dto.CellRequest;
import auca.ac.rw.dto.CellResponse;
import auca.ac.rw.dto.DistrictPatchRequest;
import auca.ac.rw.dto.DistrictRequest;
import auca.ac.rw.dto.DistrictResponse;
import auca.ac.rw.dto.ProvincePatchRequest;
import auca.ac.rw.dto.ProvinceRequest;
import auca.ac.rw.dto.ProvinceResponse;
import auca.ac.rw.dto.SectorPatchRequest;
import auca.ac.rw.dto.SectorRequest;
import auca.ac.rw.dto.SectorResponse;
import auca.ac.rw.dto.VillagePatchRequest;
import auca.ac.rw.dto.VillageRequest;
import auca.ac.rw.dto.VillageResponse;
import auca.ac.rw.entity.Cell;
import auca.ac.rw.entity.District;
import auca.ac.rw.entity.Province;
import auca.ac.rw.entity.Sector;
import auca.ac.rw.entity.Village;
import auca.ac.rw.repository.CellRepository;
import auca.ac.rw.repository.DistrictRepository;
import auca.ac.rw.repository.ProvinceRepository;
import auca.ac.rw.repository.SectorRepository;
import auca.ac.rw.repository.VillageRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LocationService {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final SectorRepository sectorRepository;
    private final CellRepository cellRepository;
    private final VillageRepository villageRepository;

    public LocationService(ProvinceRepository provinceRepository,
                           DistrictRepository districtRepository,
                           SectorRepository sectorRepository,
                           CellRepository cellRepository,
                           VillageRepository villageRepository) {
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.sectorRepository = sectorRepository;
        this.cellRepository = cellRepository;
        this.villageRepository = villageRepository;
    }

    @Transactional
    public ProvinceResponse createProvince(ProvinceRequest request) {
        validateProvinceCode(request.code(), null);

        Province province = new Province();
        province.setCode(request.code().trim());
        province.setName(request.name().trim());
        return toProvinceResponse(provinceRepository.save(province));
    }

    public ProvinceResponse getProvince(Long id) {
        return toProvinceResponse(findProvince(id));
    }

    public List<ProvinceResponse> getProvinces() {
        List<Province> provinces = provinceRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        List<ProvinceResponse> responses = new ArrayList<>();

        for (Province province : provinces) {
            responses.add(toProvinceResponse(province));
        }
        return responses;
    }

    @Transactional
    public ProvinceResponse updateProvince(Long id, ProvinceRequest request) {
        Province province = findProvince(id);
        validateProvinceCode(request.code(), province.getId());
        province.setCode(request.code().trim());
        province.setName(request.name().trim());
        return toProvinceResponse(provinceRepository.save(province));
    }

    @Transactional
    public ProvinceResponse patchProvince(Long id, ProvincePatchRequest request) {
        Province province = findProvince(id);
        String updatedCode = resolveRequiredPatchValue(province.getCode(), request.code(), "code");
        validateProvinceCode(updatedCode, province.getId());

        if (request.code() != null) {
            province.setCode(updatedCode);
        }
        if (request.name() != null) {
            province.setName(normalizeRequiredValue(request.name(), "name"));
        }
        return toProvinceResponse(provinceRepository.save(province));
    }

    @Transactional
    public void deleteProvince(Long id) {
        provinceRepository.delete(findProvince(id));
    }

    @Transactional
    public DistrictResponse createDistrict(DistrictRequest request) {
        validateDistrictCode(request.code(), null);

        District district = new District();
        district.setCode(request.code().trim());
        district.setName(request.name().trim());
        district.setProvince(findProvinceByCode(request.provinceCode()));
        return toDistrictResponse(districtRepository.save(district));
    }

    public DistrictResponse getDistrict(Long id) {
        return toDistrictResponse(findDistrict(id));
    }

    public List<DistrictResponse> getDistricts(String provinceCode) {
        List<District> districts = StringUtils.hasText(provinceCode)
                ? districtRepository.findByProvince_CodeIgnoreCaseOrderByNameAsc(provinceCode.trim())
                : districtRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        List<DistrictResponse> responses = new ArrayList<>();
        for (District district : districts) {
            responses.add(toDistrictResponse(district));
        }
        return responses;
    }

    @Transactional
    public DistrictResponse updateDistrict(Long id, DistrictRequest request) {
        District district = findDistrict(id);
        validateDistrictCode(request.code(), district.getId());
        district.setCode(request.code().trim());
        district.setName(request.name().trim());
        district.setProvince(findProvinceByCode(request.provinceCode()));
        return toDistrictResponse(districtRepository.save(district));
    }

    @Transactional
    public DistrictResponse patchDistrict(Long id, DistrictPatchRequest request) {
        District district = findDistrict(id);
        String updatedCode = resolveRequiredPatchValue(district.getCode(), request.code(), "code");
        validateDistrictCode(updatedCode, district.getId());

        if (request.code() != null) {
            district.setCode(updatedCode);
        }
        if (request.name() != null) {
            district.setName(normalizeRequiredValue(request.name(), "name"));
        }
        if (request.provinceCode() != null) {
            district.setProvince(findProvinceByCode(request.provinceCode()));
        }
        return toDistrictResponse(districtRepository.save(district));
    }

    @Transactional
    public void deleteDistrict(Long id) {
        districtRepository.delete(findDistrict(id));
    }

    @Transactional
    public SectorResponse createSector(SectorRequest request) {
        validateSectorCode(request.code(), null);

        Sector sector = new Sector();
        sector.setCode(request.code().trim());
        sector.setName(request.name().trim());
        sector.setDistrict(findDistrictByCode(request.districtCode()));
        return toSectorResponse(sectorRepository.save(sector));
    }

    public SectorResponse getSector(Long id) {
        return toSectorResponse(findSector(id));
    }

    public List<SectorResponse> getSectors(String districtCode) {
        List<Sector> sectors = StringUtils.hasText(districtCode)
                ? sectorRepository.findByDistrict_CodeIgnoreCaseOrderByNameAsc(districtCode.trim())
                : sectorRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        List<SectorResponse> responses = new ArrayList<>();
        for (Sector sector : sectors) {
            responses.add(toSectorResponse(sector));
        }
        return responses;
    }

    @Transactional
    public SectorResponse updateSector(Long id, SectorRequest request) {
        Sector sector = findSector(id);
        validateSectorCode(request.code(), sector.getId());
        sector.setCode(request.code().trim());
        sector.setName(request.name().trim());
        sector.setDistrict(findDistrictByCode(request.districtCode()));
        return toSectorResponse(sectorRepository.save(sector));
    }

    @Transactional
    public SectorResponse patchSector(Long id, SectorPatchRequest request) {
        Sector sector = findSector(id);
        String updatedCode = resolveRequiredPatchValue(sector.getCode(), request.code(), "code");
        validateSectorCode(updatedCode, sector.getId());

        if (request.code() != null) {
            sector.setCode(updatedCode);
        }
        if (request.name() != null) {
            sector.setName(normalizeRequiredValue(request.name(), "name"));
        }
        if (request.districtCode() != null) {
            sector.setDistrict(findDistrictByCode(request.districtCode()));
        }
        return toSectorResponse(sectorRepository.save(sector));
    }

    @Transactional
    public void deleteSector(Long id) {
        sectorRepository.delete(findSector(id));
    }

    @Transactional
    public CellResponse createCell(CellRequest request) {
        validateCellCode(request.code(), null);

        Cell cell = new Cell();
        cell.setCode(request.code().trim());
        cell.setName(request.name().trim());
        cell.setSector(findSectorByCode(request.sectorCode()));
        return toCellResponse(cellRepository.save(cell));
    }

    public CellResponse getCell(Long id) {
        return toCellResponse(findCell(id));
    }

    public List<CellResponse> getCells(String sectorCode) {
        List<Cell> cells = StringUtils.hasText(sectorCode)
                ? cellRepository.findBySector_CodeIgnoreCaseOrderByNameAsc(sectorCode.trim())
                : cellRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        List<CellResponse> responses = new ArrayList<>();
        for (Cell cell : cells) {
            responses.add(toCellResponse(cell));
        }
        return responses;
    }

    @Transactional
    public CellResponse updateCell(Long id, CellRequest request) {
        Cell cell = findCell(id);
        validateCellCode(request.code(), cell.getId());
        cell.setCode(request.code().trim());
        cell.setName(request.name().trim());
        cell.setSector(findSectorByCode(request.sectorCode()));
        return toCellResponse(cellRepository.save(cell));
    }

    @Transactional
    public CellResponse patchCell(Long id, CellPatchRequest request) {
        Cell cell = findCell(id);
        String updatedCode = resolveRequiredPatchValue(cell.getCode(), request.code(), "code");
        validateCellCode(updatedCode, cell.getId());

        if (request.code() != null) {
            cell.setCode(updatedCode);
        }
        if (request.name() != null) {
            cell.setName(normalizeRequiredValue(request.name(), "name"));
        }
        if (request.sectorCode() != null) {
            cell.setSector(findSectorByCode(request.sectorCode()));
        }
        return toCellResponse(cellRepository.save(cell));
    }

    @Transactional
    public void deleteCell(Long id) {
        cellRepository.delete(findCell(id));
    }

    @Transactional
    public VillageResponse createVillage(VillageRequest request) {
        validateVillageCode(request.code(), null);

        Village village = new Village();
        village.setCode(request.code().trim());
        village.setName(request.name().trim());
        village.setCell(findCellByCode(request.cellCode()));
        return toVillageResponse(villageRepository.save(village));
    }

    public VillageResponse getVillage(Long id) {
        return toVillageResponse(findVillage(id));
    }

    public List<VillageResponse> getVillages(String cellCode) {
        List<Village> villages = StringUtils.hasText(cellCode)
                ? villageRepository.findByCell_CodeIgnoreCaseOrderByNameAsc(cellCode.trim())
                : villageRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        List<VillageResponse> responses = new ArrayList<>();
        for (Village village : villages) {
            responses.add(toVillageResponse(village));
        }
        return responses;
    }

    @Transactional
    public VillageResponse updateVillage(Long id, VillageRequest request) {
        Village village = findVillage(id);
        validateVillageCode(request.code(), village.getId());
        village.setCode(request.code().trim());
        village.setName(request.name().trim());
        village.setCell(findCellByCode(request.cellCode()));
        return toVillageResponse(villageRepository.save(village));
    }

    @Transactional
    public VillageResponse patchVillage(Long id, VillagePatchRequest request) {
        Village village = findVillage(id);
        String updatedCode = resolveRequiredPatchValue(village.getCode(), request.code(), "code");
        validateVillageCode(updatedCode, village.getId());

        if (request.code() != null) {
            village.setCode(updatedCode);
        }
        if (request.name() != null) {
            village.setName(normalizeRequiredValue(request.name(), "name"));
        }
        if (request.cellCode() != null) {
            village.setCell(findCellByCode(request.cellCode()));
        }
        return toVillageResponse(villageRepository.save(village));
    }

    @Transactional
    public void deleteVillage(Long id) {
        villageRepository.delete(findVillage(id));
    }

    private Province findProvince(Long id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Province not found."));
    }

    private District findDistrict(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "District not found."));
    }

    private Sector findSector(Long id) {
        return sectorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector not found."));
    }

    private Cell findCell(Long id) {
        return cellRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cell not found."));
    }

    private Village findVillage(Long id) {
        return villageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Village not found."));
    }

    private Province findProvinceByCode(String provinceCode) {
        return provinceRepository.findByCodeIgnoreCase(normalizeRequiredValue(provinceCode, "provinceCode"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Province not found."));
    }

    private District findDistrictByCode(String districtCode) {
        return districtRepository.findByCodeIgnoreCase(normalizeRequiredValue(districtCode, "districtCode"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "District not found."));
    }

    private Sector findSectorByCode(String sectorCode) {
        return sectorRepository.findByCodeIgnoreCase(normalizeRequiredValue(sectorCode, "sectorCode"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector not found."));
    }

    private Cell findCellByCode(String cellCode) {
        return cellRepository.findByCodeIgnoreCase(normalizeRequiredValue(cellCode, "cellCode"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cell not found."));
    }

    private void validateProvinceCode(String code, Long currentId) {
        String normalizedCode = normalizeRequiredValue(code, "code");
        if (provinceRepository.existsByCodeIgnoreCase(normalizedCode)) {
            Province existing = provinceRepository.findByCodeIgnoreCase(normalizedCode).orElse(null);
            if (existing != null && !existing.getId().equals(currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Province code already exists.");
            }
        }
    }

    private void validateDistrictCode(String code, Long currentId) {
        String normalizedCode = normalizeRequiredValue(code, "code");
        if (districtRepository.existsByCodeIgnoreCase(normalizedCode)) {
            District existing = districtRepository.findByCodeIgnoreCase(normalizedCode).orElse(null);
            if (existing != null && !existing.getId().equals(currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "District code already exists.");
            }
        }
    }

    private void validateSectorCode(String code, Long currentId) {
        String normalizedCode = normalizeRequiredValue(code, "code");
        if (sectorRepository.existsByCodeIgnoreCase(normalizedCode)) {
            Sector existing = sectorRepository.findByCodeIgnoreCase(normalizedCode).orElse(null);
            if (existing != null && !existing.getId().equals(currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Sector code already exists.");
            }
        }
    }

    private void validateCellCode(String code, Long currentId) {
        String normalizedCode = normalizeRequiredValue(code, "code");
        if (cellRepository.existsByCodeIgnoreCase(normalizedCode)) {
            Cell existing = cellRepository.findByCodeIgnoreCase(normalizedCode).orElse(null);
            if (existing != null && !existing.getId().equals(currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cell code already exists.");
            }
        }
    }

    private void validateVillageCode(String code, Long currentId) {
        String normalizedCode = normalizeRequiredValue(code, "code");
        if (villageRepository.existsByCodeIgnoreCase(normalizedCode)) {
            Village existing = villageRepository.findByCodeIgnoreCase(normalizedCode).orElse(null);
            if (existing != null && !existing.getId().equals(currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Village code already exists.");
            }
        }
    }

    private ProvinceResponse toProvinceResponse(Province province) {
        return new ProvinceResponse(province.getId(), province.getCode(), province.getName());
    }

    private DistrictResponse toDistrictResponse(District district) {
        return new DistrictResponse(
                district.getId(),
                district.getCode(),
                district.getName(),
                district.getProvince().getCode(),
                district.getProvince().getName()
        );
    }

    private SectorResponse toSectorResponse(Sector sector) {
        District district = sector.getDistrict();
        Province province = sector.getProvince();

        return new SectorResponse(
                sector.getId(),
                sector.getCode(),
                sector.getName(),
                district.getCode(),
                district.getName(),
                province.getCode(),
                province.getName()
        );
    }

    private CellResponse toCellResponse(Cell cell) {
        Sector sector = cell.getSector();
        District district = cell.getDistrict();
        Province province = cell.getProvince();

        return new CellResponse(
                cell.getId(),
                cell.getCode(),
                cell.getName(),
                sector.getCode(),
                sector.getName(),
                district.getCode(),
                district.getName(),
                province.getCode(),
                province.getName()
        );
    }

    private VillageResponse toVillageResponse(Village village) {
        Cell cell = village.getCell();
        Sector sector = village.getSector();
        District district = village.getDistrict();
        Province province = village.getProvince();

        return new VillageResponse(
                village.getId(),
                village.getCode(),
                village.getName(),
                cell.getCode(),
                cell.getName(),
                sector.getCode(),
                sector.getName(),
                district.getCode(),
                district.getName(),
                province.getCode(),
                province.getName()
        );
    }

    private String resolveRequiredPatchValue(String currentValue, String patchedValue, String fieldName) {
        if (patchedValue == null) {
            return currentValue;
        }
        return normalizeRequiredValue(patchedValue, fieldName);
    }

    private String normalizeRequiredValue(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must not be blank.");
        }
        return value.trim();
    }
}

package auca.ac.rw.controller;

import auca.ac.rw.dto.CellRequest;
import auca.ac.rw.dto.CellResponse;
import auca.ac.rw.dto.CellPatchRequest;
import auca.ac.rw.dto.DistrictRequest;
import auca.ac.rw.dto.DistrictResponse;
import auca.ac.rw.dto.DistrictPatchRequest;
import auca.ac.rw.dto.ProvinceRequest;
import auca.ac.rw.dto.ProvinceResponse;
import auca.ac.rw.dto.ProvincePatchRequest;
import auca.ac.rw.dto.SectorRequest;
import auca.ac.rw.dto.SectorResponse;
import auca.ac.rw.dto.SectorPatchRequest;
import auca.ac.rw.dto.VillageRequest;
import auca.ac.rw.dto.VillageResponse;
import auca.ac.rw.dto.VillagePatchRequest;
import auca.ac.rw.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/provinces")
    @ResponseStatus(HttpStatus.CREATED)
    public ProvinceResponse createProvince(@Valid @RequestBody ProvinceRequest request) {
        return locationService.createProvince(request);
    }

    @GetMapping("/provinces/{id}")
    public ProvinceResponse getProvince(@PathVariable Long id) {
        return locationService.getProvince(id);
    }

    @GetMapping("/provinces")
    public List<ProvinceResponse> getProvinces() {
        return locationService.getProvinces();
    }

    @PutMapping("/provinces/{id}")
    public ProvinceResponse updateProvince(@PathVariable Long id, @Valid @RequestBody ProvinceRequest request) {
        return locationService.updateProvince(id, request);
    }

    @PatchMapping("/provinces/{id}")
    public ProvinceResponse patchProvince(@PathVariable Long id, @Valid @RequestBody ProvincePatchRequest request) {
        return locationService.patchProvince(id, request);
    }

    @DeleteMapping("/provinces/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProvince(@PathVariable Long id) {
        locationService.deleteProvince(id);
    }

    @PostMapping("/districts")
    @ResponseStatus(HttpStatus.CREATED)
    public DistrictResponse createDistrict(@Valid @RequestBody DistrictRequest request) {
        return locationService.createDistrict(request);
    }

    @GetMapping("/districts/{id}")
    public DistrictResponse getDistrict(@PathVariable Long id) {
        return locationService.getDistrict(id);
    }

    @GetMapping("/districts")
    public List<DistrictResponse> getDistricts(@RequestParam(required = false) String provinceCode) {
        return locationService.getDistricts(provinceCode);
    }

    @PutMapping("/districts/{id}")
    public DistrictResponse updateDistrict(@PathVariable Long id, @Valid @RequestBody DistrictRequest request) {
        return locationService.updateDistrict(id, request);
    }

    @PatchMapping("/districts/{id}")
    public DistrictResponse patchDistrict(@PathVariable Long id, @Valid @RequestBody DistrictPatchRequest request) {
        return locationService.patchDistrict(id, request);
    }

    @DeleteMapping("/districts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDistrict(@PathVariable Long id) {
        locationService.deleteDistrict(id);
    }

    @PostMapping("/sectors")
    @ResponseStatus(HttpStatus.CREATED)
    public SectorResponse createSector(@Valid @RequestBody SectorRequest request) {
        return locationService.createSector(request);
    }

    @GetMapping("/sectors/{id}")
    public SectorResponse getSector(@PathVariable Long id) {
        return locationService.getSector(id);
    }

    @GetMapping("/sectors")
    public List<SectorResponse> getSectors(@RequestParam(required = false) String districtCode) {
        return locationService.getSectors(districtCode);
    }

    @PutMapping("/sectors/{id}")
    public SectorResponse updateSector(@PathVariable Long id, @Valid @RequestBody SectorRequest request) {
        return locationService.updateSector(id, request);
    }

    @PatchMapping("/sectors/{id}")
    public SectorResponse patchSector(@PathVariable Long id, @Valid @RequestBody SectorPatchRequest request) {
        return locationService.patchSector(id, request);
    }

    @DeleteMapping("/sectors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSector(@PathVariable Long id) {
        locationService.deleteSector(id);
    }

    @PostMapping("/cells")
    @ResponseStatus(HttpStatus.CREATED)
    public CellResponse createCell(@Valid @RequestBody CellRequest request) {
        return locationService.createCell(request);
    }

    @GetMapping("/cells/{id}")
    public CellResponse getCell(@PathVariable Long id) {
        return locationService.getCell(id);
    }

    @GetMapping("/cells")
    public List<CellResponse> getCells(@RequestParam(required = false) String sectorCode) {
        return locationService.getCells(sectorCode);
    }

    @PutMapping("/cells/{id}")
    public CellResponse updateCell(@PathVariable Long id, @Valid @RequestBody CellRequest request) {
        return locationService.updateCell(id, request);
    }

    @PatchMapping("/cells/{id}")
    public CellResponse patchCell(@PathVariable Long id, @Valid @RequestBody CellPatchRequest request) {
        return locationService.patchCell(id, request);
    }

    @DeleteMapping("/cells/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCell(@PathVariable Long id) {
        locationService.deleteCell(id);
    }

    @PostMapping("/villages")
    @ResponseStatus(HttpStatus.CREATED)
    public VillageResponse createVillage(@Valid @RequestBody VillageRequest request) {
        return locationService.createVillage(request);
    }

    @GetMapping("/villages/{id}")
    public VillageResponse getVillage(@PathVariable Long id) {
        return locationService.getVillage(id);
    }

    @GetMapping("/villages")
    public List<VillageResponse> getVillages(@RequestParam(required = false) String cellCode) {
        return locationService.getVillages(cellCode);
    }

    @PutMapping("/villages/{id}")
    public VillageResponse updateVillage(@PathVariable Long id, @Valid @RequestBody VillageRequest request) {
        return locationService.updateVillage(id, request);
    }

    @PatchMapping("/villages/{id}")
    public VillageResponse patchVillage(@PathVariable Long id, @Valid @RequestBody VillagePatchRequest request) {
        return locationService.patchVillage(id, request);
    }

    @DeleteMapping("/villages/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVillage(@PathVariable Long id) {
        locationService.deleteVillage(id);
    }
}

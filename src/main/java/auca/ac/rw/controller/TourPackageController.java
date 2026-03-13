package auca.ac.rw.controller;

import auca.ac.rw.dto.PageResponse;
import auca.ac.rw.dto.TourPackagePatchRequest;
import auca.ac.rw.dto.TourPackageRequest;
import auca.ac.rw.dto.TourPackageResponse;
import auca.ac.rw.service.TourPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/packages")
public class TourPackageController {

    private final TourPackageService tourPackageService;

    public TourPackageController(TourPackageService tourPackageService) {
        this.tourPackageService = tourPackageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TourPackageResponse createPackage(@Valid @RequestBody TourPackageRequest request) {
        return tourPackageService.createPackage(request);
    }

    @PutMapping("/{id}")
    public TourPackageResponse updatePackage(@PathVariable Long id, @Valid @RequestBody TourPackageRequest request) {
        return tourPackageService.updatePackage(id, request);
    }

    @PatchMapping("/{id}")
    public TourPackageResponse patchPackage(@PathVariable Long id, @Valid @RequestBody TourPackagePatchRequest request) {
        return tourPackageService.patchPackage(id, request);
    }

    @GetMapping("/{id}")
    public TourPackageResponse getPackage(@PathVariable Long id) {
        return tourPackageService.getPackage(id);
    }

    @GetMapping
    public PageResponse<TourPackageResponse> getPackages(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "title") String sortBy,
                                                         @RequestParam(defaultValue = "asc") String direction) {
        return tourPackageService.getPackages(page, size, sortBy, direction);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePackage(@PathVariable Long id) {
        tourPackageService.deletePackage(id);
    }
}

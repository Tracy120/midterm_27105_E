package auca.ac.rw.tourbook.controller;

import auca.ac.rw.tourbook.model.TourPackage;
import auca.ac.rw.tourbook.service.TourPackageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TourPackageController {

    private final TourPackageService tourPackageService;

    public TourPackageController(TourPackageService tourPackageService) {
        this.tourPackageService = tourPackageService;
    }

    @PostMapping("/package/save")
    public ResponseEntity<?> savePackage(@Valid @RequestBody TourPackage tourPackage,
                                         @RequestParam(required = false) List<Long> guideIds) {
        Object response = tourPackageService.savePackage(tourPackage, guideIds);
        if (response instanceof TourPackage) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/packages/all")
    public ResponseEntity<Page<TourPackage>> getAllPackages(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(tourPackageService.getAllPackages(page, size, sortBy));
    }

    @GetMapping("/package/{id}")
    public ResponseEntity<TourPackage> getPackageById(@PathVariable Long id) {
        return tourPackageService.getPackageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/package/update/{id}")
    public ResponseEntity<?> updatePackage(@PathVariable Long id,
                                           @Valid @RequestBody TourPackage tourPackage,
                                           @RequestParam(required = false) List<Long> guideIds) {
        Object response = tourPackageService.updatePackage(id, tourPackage, guideIds);
        if (response instanceof TourPackage) {
            return ResponseEntity.ok(response);
        }
        if (response.toString().contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("/package/delete/{id}")
    public ResponseEntity<String> deletePackage(@PathVariable Long id) {
        String result = tourPackageService.deletePackage(id);
        return result.startsWith("Error:")
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(result)
                : ResponseEntity.ok(result);
    }
}

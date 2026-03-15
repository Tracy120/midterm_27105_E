package auca.ac.rw.tourbook.controller;

import auca.ac.rw.tourbook.model.TourGuide;
import auca.ac.rw.tourbook.service.TourGuideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TourGuideController {

    private final TourGuideService tourGuideService;

    public TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @PostMapping("/guide/save")
    public ResponseEntity<?> saveGuide(@Valid @RequestBody TourGuide guide) {
        Object response = tourGuideService.saveGuide(guide);
        if (response instanceof TourGuide) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/guides/all")
    public ResponseEntity<List<TourGuide>> getAllGuides() {
        return ResponseEntity.ok(tourGuideService.getAllGuides());
    }

    @GetMapping("/guide/{id}")
    public ResponseEntity<TourGuide> getGuideById(@PathVariable("id") Long id) {
        return tourGuideService.getGuideById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/guide/update/{id}")
    public ResponseEntity<?> updateGuide(@PathVariable("id") Long id, @Valid @RequestBody TourGuide guide) {
        Object response = tourGuideService.updateGuide(id, guide);
        if (response instanceof TourGuide) {
            return ResponseEntity.ok(response);
        }
        if (response.toString().contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("/guide/delete/{id}")
    public ResponseEntity<String> deleteGuide(@PathVariable("id") Long id) {
        String result = tourGuideService.deleteGuide(id);
        return result.startsWith("Error:")
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(result)
                : ResponseEntity.ok(result);
    }
}

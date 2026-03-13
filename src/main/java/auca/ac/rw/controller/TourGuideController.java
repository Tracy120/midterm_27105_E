package auca.ac.rw.controller;

import auca.ac.rw.dto.TourGuidePatchRequest;
import auca.ac.rw.dto.TourGuideRequest;
import auca.ac.rw.dto.TourGuideResponse;
import auca.ac.rw.service.TourGuideService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
public class TourGuideController {

    private final TourGuideService tourGuideService;

    public TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TourGuideResponse createGuide(@Valid @RequestBody TourGuideRequest request) {
        return tourGuideService.createGuide(request);
    }

    @PutMapping("/{id}")
    public TourGuideResponse updateGuide(@PathVariable Long id, @Valid @RequestBody TourGuideRequest request) {
        return tourGuideService.updateGuide(id, request);
    }

    @PatchMapping("/{id}")
    public TourGuideResponse patchGuide(@PathVariable Long id, @Valid @RequestBody TourGuidePatchRequest request) {
        return tourGuideService.patchGuide(id, request);
    }

    @GetMapping
    public List<TourGuideResponse> getGuides() {
        return tourGuideService.getGuides();
    }

    @GetMapping("/{id}")
    public TourGuideResponse getGuide(@PathVariable Long id) {
        return tourGuideService.getGuide(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGuide(@PathVariable Long id) {
        tourGuideService.deleteGuide(id);
    }
}

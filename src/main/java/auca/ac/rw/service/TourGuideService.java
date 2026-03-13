package auca.ac.rw.service;

import auca.ac.rw.dto.TourGuidePatchRequest;
import auca.ac.rw.dto.TourGuideRequest;
import auca.ac.rw.dto.TourGuideResponse;
import auca.ac.rw.entity.TourGuide;
import auca.ac.rw.entity.TourPackage;
import auca.ac.rw.repository.TourGuideRepository;
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
public class TourGuideService {

    private final TourGuideRepository tourGuideRepository;

    public TourGuideService(TourGuideRepository tourGuideRepository) {
        this.tourGuideRepository = tourGuideRepository;
    }

    @Transactional
    public TourGuideResponse createGuide(TourGuideRequest request) {
        TourGuide guide = new TourGuide();
        validateUniqueGuideFields(request.guideCode(), request.email(), null);
        applyGuideChanges(guide, request);
        return toResponse(tourGuideRepository.save(guide));
    }

    @Transactional
    public TourGuideResponse updateGuide(Long id, TourGuideRequest request) {
        TourGuide guide = findGuide(id);
        validateUniqueGuideFields(request.guideCode(), request.email(), guide.getId());
        applyGuideChanges(guide, request);
        return toResponse(tourGuideRepository.save(guide));
    }

    @Transactional
    public TourGuideResponse patchGuide(Long id, TourGuidePatchRequest request) {
        TourGuide guide = findGuide(id);
        String updatedGuideCode = resolveRequiredPatchValue(guide.getGuideCode(), request.guideCode(), "guideCode");
        String updatedEmail = resolveRequiredPatchValue(guide.getEmail(), request.email(), "email");
        validateUniqueGuideFields(updatedGuideCode, updatedEmail, guide.getId());
        applyGuidePatch(guide, request);
        return toResponse(tourGuideRepository.save(guide));
    }

    public List<TourGuideResponse> getGuides() {
        List<TourGuide> guides = tourGuideRepository.findAll(Sort.by(Sort.Direction.ASC, "fullName"));
        List<TourGuideResponse> responses = new ArrayList<>();

        for (TourGuide guide : guides) {
            responses.add(toResponse(guide));
        }

        return responses;
    }

    public TourGuideResponse getGuide(Long id) {
        return toResponse(findGuide(id));
    }

    @Transactional
    public void deleteGuide(Long id) {
        TourGuide guide = findGuide(id);
        for (TourPackage tourPackage : new ArrayList<>(guide.getPackages())) {
            tourPackage.getGuides().remove(guide);
        }
        tourGuideRepository.delete(guide);
    }

    public TourGuide findGuide(Long id) {
        return tourGuideRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide not found."));
    }

    public TourGuideResponse toResponse(TourGuide guide) {
        return new TourGuideResponse(
                guide.getId(),
                guide.getGuideCode(),
                guide.getFullName(),
                guide.getEmail(),
                guide.getPhoneNumber(),
                guide.getSpecialization()
        );
    }

    private void applyGuideChanges(TourGuide guide, TourGuideRequest request) {
        guide.setGuideCode(request.guideCode().trim());
        guide.setFullName(request.fullName().trim());
        guide.setEmail(request.email().trim());
        guide.setPhoneNumber(request.phoneNumber().trim());
        guide.setSpecialization(request.specialization());
    }

    private void applyGuidePatch(TourGuide guide, TourGuidePatchRequest request) {
        if (request.guideCode() != null) {
            guide.setGuideCode(normalizeRequiredValue(request.guideCode(), "guideCode"));
        }
        if (request.fullName() != null) {
            guide.setFullName(normalizeRequiredValue(request.fullName(), "fullName"));
        }
        if (request.email() != null) {
            guide.setEmail(normalizeRequiredValue(request.email(), "email"));
        }
        if (request.phoneNumber() != null) {
            guide.setPhoneNumber(normalizeRequiredValue(request.phoneNumber(), "phoneNumber"));
        }
        if (request.specialization() != null) {
            guide.setSpecialization(normalizeOptionalValue(request.specialization()));
        }
    }

    private void validateUniqueGuideFields(String guideCode, String email, Long currentGuideId) {
        if (tourGuideRepository.existsByGuideCodeIgnoreCase(guideCode)) {
            TourGuide guideWithCode = tourGuideRepository.findByGuideCodeIgnoreCase(guideCode);
            if (guideWithCode != null && !guideWithCode.getId().equals(currentGuideId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Guide code already exists.");
            }
        }

        if (tourGuideRepository.existsByEmailIgnoreCase(email)) {
            TourGuide guideWithEmail = tourGuideRepository.findByEmailIgnoreCase(email);
            if (guideWithEmail != null && !guideWithEmail.getId().equals(currentGuideId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Guide email already exists.");
            }
        }
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

    private String normalizeOptionalValue(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

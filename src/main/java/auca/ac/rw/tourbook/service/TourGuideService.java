package auca.ac.rw.tourbook.service;

import auca.ac.rw.tourbook.model.TourGuide;
import auca.ac.rw.tourbook.model.TourPackage;
import auca.ac.rw.tourbook.repository.TourGuideRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TourGuideService {

    private final TourGuideRepository tourGuideRepository;

    public TourGuideService(TourGuideRepository tourGuideRepository) {
        this.tourGuideRepository = tourGuideRepository;
    }

    public Object saveGuide(TourGuide guide) {
        if (guide == null) {
            return "Error: Guide payload is required.";
        }
        if (!StringUtils.hasText(guide.getGuideCode())) {
            return "Error: Guide code is required.";
        }
        if (!StringUtils.hasText(guide.getEmail())) {
            return "Error: Guide email is required.";
        }
        if (tourGuideRepository.existsByGuideCodeIgnoreCase(guide.getGuideCode().trim())) {
            return "Error: Guide code already exists.";
        }
        if (tourGuideRepository.existsByEmailIgnoreCase(guide.getEmail().trim())) {
            return "Error: Guide email already exists.";
        }

        normalizeGuide(guide);
        return tourGuideRepository.save(guide);
    }

    public List<TourGuide> getAllGuides() {
        return tourGuideRepository.findAll(Sort.by("fullName"));
    }

    public Optional<TourGuide> getGuideById(Long id) {
        return tourGuideRepository.findById(id);
    }

    public Object updateGuide(Long id, TourGuide newGuideData) {
        Optional<TourGuide> existingGuideOpt = tourGuideRepository.findById(id);
        if (!existingGuideOpt.isPresent() || newGuideData == null) {
            return "Error: Guide not found.";
        }

        TourGuide existingGuide = existingGuideOpt.get();
        normalizeGuide(newGuideData);

        TourGuide guideWithSameCode = tourGuideRepository.findByGuideCodeIgnoreCase(newGuideData.getGuideCode());
        if (guideWithSameCode != null && !guideWithSameCode.getId().equals(existingGuide.getId())) {
            return "Error: Guide code already exists.";
        }

        TourGuide guideWithSameEmail = tourGuideRepository.findByEmailIgnoreCase(newGuideData.getEmail());
        if (guideWithSameEmail != null && !guideWithSameEmail.getId().equals(existingGuide.getId())) {
            return "Error: Guide email already exists.";
        }

        existingGuide.setGuideCode(newGuideData.getGuideCode());
        existingGuide.setFullName(newGuideData.getFullName());
        existingGuide.setEmail(newGuideData.getEmail());
        existingGuide.setPhoneNumber(newGuideData.getPhoneNumber());
        existingGuide.setSpecialization(normalizeOptionalValue(newGuideData.getSpecialization()));
        return tourGuideRepository.save(existingGuide);
    }

    public String deleteGuide(Long id) {
        Optional<TourGuide> guideOpt = tourGuideRepository.findById(id);
        if (!guideOpt.isPresent()) {
            return "Error: Guide not found.";
        }

        TourGuide guide = guideOpt.get();
        for (TourPackage tourPackage : new ArrayList<>(guide.getPackages())) {
            tourPackage.getGuides().remove(guide);
        }
        tourGuideRepository.delete(guide);
        return "Success: Guide deleted.";
    }

    public TourGuide findGuide(Long id) {
        return tourGuideRepository.findById(id).orElse(null);
    }

    private void normalizeGuide(TourGuide guide) {
        if (StringUtils.hasText(guide.getGuideCode())) {
            guide.setGuideCode(guide.getGuideCode().trim());
        }
        if (StringUtils.hasText(guide.getFullName())) {
            guide.setFullName(guide.getFullName().trim());
        }
        if (StringUtils.hasText(guide.getEmail())) {
            guide.setEmail(guide.getEmail().trim());
        }
        if (StringUtils.hasText(guide.getPhoneNumber())) {
            guide.setPhoneNumber(guide.getPhoneNumber().trim());
        }
        guide.setSpecialization(normalizeOptionalValue(guide.getSpecialization()));
    }

    private String normalizeOptionalValue(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

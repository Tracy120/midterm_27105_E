package auca.ac.rw.tourbook.service;

import auca.ac.rw.tourbook.model.TourGuide;
import auca.ac.rw.tourbook.model.TourPackage;
import auca.ac.rw.tourbook.repository.TourGuideRepository;
import auca.ac.rw.tourbook.repository.TourPackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TourPackageService {

    private final TourPackageRepository tourPackageRepository;
    private final TourGuideRepository tourGuideRepository;

    public TourPackageService(TourPackageRepository tourPackageRepository, TourGuideRepository tourGuideRepository) {
        this.tourPackageRepository = tourPackageRepository;
        this.tourGuideRepository = tourGuideRepository;
    }

    public Object savePackage(TourPackage tourPackage, List<Long> guideIds) {
        if (tourPackage == null) {
            return "Error: Package payload is required.";
        }
        if (!StringUtils.hasText(tourPackage.getPackageCode())) {
            return "Error: Package code is required.";
        }
        if (!StringUtils.hasText(tourPackage.getTitle())) {
            return "Error: Package title is required.";
        }
        if (tourPackageRepository.existsByPackageCodeIgnoreCase(tourPackage.getPackageCode().trim())) {
            return "Error: Package code already exists.";
        }
        if (tourPackageRepository.existsByTitleIgnoreCase(tourPackage.getTitle().trim())) {
            return "Error: Package title already exists.";
        }

        normalizePackage(tourPackage);
        Object guides = resolveGuides(guideIds);
        if (guides instanceof String) {
            return guides;
        }
        tourPackage.getGuides().clear();
        tourPackage.getGuides().addAll((Set<TourGuide>) guides);
        return tourPackageRepository.save(tourPackage);
    }

    public Page<TourPackage> getAllPackages(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return tourPackageRepository.findAll(pageable);
    }

    public Optional<TourPackage> getPackageById(Long id) {
        return tourPackageRepository.findById(id);
    }

    public Object updatePackage(Long id, TourPackage newPackageData, List<Long> guideIds) {
        Optional<TourPackage> existingPackageOpt = tourPackageRepository.findById(id);
        if (!existingPackageOpt.isPresent() || newPackageData == null) {
            return "Error: Tour package not found.";
        }

        TourPackage existingPackage = existingPackageOpt.get();
        normalizePackage(newPackageData);

        TourPackage packageWithSameCode = tourPackageRepository.findByPackageCodeIgnoreCase(newPackageData.getPackageCode());
        if (packageWithSameCode != null && !packageWithSameCode.getId().equals(existingPackage.getId())) {
            return "Error: Package code already exists.";
        }

        TourPackage packageWithSameTitle = tourPackageRepository.findByTitleIgnoreCase(newPackageData.getTitle());
        if (packageWithSameTitle != null && !packageWithSameTitle.getId().equals(existingPackage.getId())) {
            return "Error: Package title already exists.";
        }

        existingPackage.setPackageCode(newPackageData.getPackageCode());
        existingPackage.setTitle(newPackageData.getTitle());
        existingPackage.setDescription(newPackageData.getDescription());
        existingPackage.setPrice(newPackageData.getPrice());
        existingPackage.setDurationInDays(newPackageData.getDurationInDays());

        if (guideIds != null) {
            Object guides = resolveGuides(guideIds);
            if (guides instanceof String) {
                return guides;
            }
            existingPackage.getGuides().clear();
            existingPackage.getGuides().addAll((Set<TourGuide>) guides);
        }

        return tourPackageRepository.save(existingPackage);
    }

    public String deletePackage(Long id) {
        Optional<TourPackage> tourPackageOpt = tourPackageRepository.findById(id);
        if (!tourPackageOpt.isPresent()) {
            return "Error: Tour package not found.";
        }

        TourPackage tourPackage = tourPackageOpt.get();
        tourPackage.getGuides().clear();
        tourPackageRepository.delete(tourPackage);
        return "Success: Tour package deleted.";
    }

    public TourPackage findPackage(Long id) {
        return tourPackageRepository.findById(id).orElse(null);
    }

    private void normalizePackage(TourPackage tourPackage) {
        if (StringUtils.hasText(tourPackage.getPackageCode())) {
            tourPackage.setPackageCode(tourPackage.getPackageCode().trim());
        }
        if (StringUtils.hasText(tourPackage.getTitle())) {
            tourPackage.setTitle(tourPackage.getTitle().trim());
        }
        if (StringUtils.hasText(tourPackage.getDescription())) {
            tourPackage.setDescription(tourPackage.getDescription().trim());
        }
    }

    private Object resolveGuides(List<Long> guideIds) {
        if (guideIds == null || guideIds.isEmpty()) {
            return Collections.emptySet();
        }

        List<TourGuide> guides = tourGuideRepository.findAllById(guideIds);
        if (guides.size() != guideIds.size()) {
            return "Error: One or more guide IDs were not found.";
        }
        return new LinkedHashSet<>(guides);
    }
}

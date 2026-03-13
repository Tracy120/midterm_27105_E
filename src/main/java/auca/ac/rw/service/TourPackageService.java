package auca.ac.rw.service;

import auca.ac.rw.dto.PageResponse;
import auca.ac.rw.dto.TourGuideResponse;
import auca.ac.rw.dto.TourPackagePatchRequest;
import auca.ac.rw.dto.TourPackageRequest;
import auca.ac.rw.dto.TourPackageResponse;
import auca.ac.rw.dto.TourPackageSummary;
import auca.ac.rw.entity.TourGuide;
import auca.ac.rw.entity.TourPackage;
import auca.ac.rw.repository.TourGuideRepository;
import auca.ac.rw.repository.TourPackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class TourPackageService {

    private static final Set<String> PACKAGE_SORT_FIELDS = Set.of("title", "price", "durationInDays");

    private final TourPackageRepository tourPackageRepository;
    private final TourGuideRepository tourGuideRepository;
    private final TourGuideService tourGuideService;

    public TourPackageService(TourPackageRepository tourPackageRepository,
                              TourGuideRepository tourGuideRepository,
                              TourGuideService tourGuideService) {
        this.tourPackageRepository = tourPackageRepository;
        this.tourGuideRepository = tourGuideRepository;
        this.tourGuideService = tourGuideService;
    }

    @Transactional
    public TourPackageResponse createPackage(TourPackageRequest request) {
        TourPackage tourPackage = new TourPackage();
        validateUniquePackageFields(request.packageCode(), request.title(), null);
        applyPackageChanges(tourPackage, request);
        return toResponse(tourPackageRepository.save(tourPackage));
    }

    @Transactional
    public TourPackageResponse updatePackage(Long id, TourPackageRequest request) {
        TourPackage tourPackage = findPackage(id);
        validateUniquePackageFields(request.packageCode(), request.title(), tourPackage.getId());
        applyPackageChanges(tourPackage, request);
        return toResponse(tourPackageRepository.save(tourPackage));
    }

    @Transactional
    public TourPackageResponse patchPackage(Long id, TourPackagePatchRequest request) {
        TourPackage tourPackage = findPackage(id);
        String updatedPackageCode = resolveRequiredPatchValue(tourPackage.getPackageCode(), request.packageCode(), "packageCode");
        String updatedTitle = resolveRequiredPatchValue(tourPackage.getTitle(), request.title(), "title");
        validateUniquePackageFields(updatedPackageCode, updatedTitle, tourPackage.getId());
        applyPackagePatch(tourPackage, request);
        return toResponse(tourPackageRepository.save(tourPackage));
    }

    public TourPackageResponse getPackage(Long id) {
        return toResponse(findPackage(id));
    }

    public PageResponse<TourPackageResponse> getPackages(int page, int size, String sortBy, String direction) {
        String resolvedSortBy = resolveSortField(sortBy, PACKAGE_SORT_FIELDS, "title");
        Sort.Direction sortDirection = resolveDirection(direction);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, resolvedSortBy));
        Page<TourPackage> packagePage = tourPackageRepository.findAll(pageRequest);
        List<TourPackageResponse> packages = new ArrayList<>();

        for (TourPackage tourPackage : packagePage.getContent()) {
            packages.add(toResponse(tourPackage));
        }

        return new PageResponse<>(
                packages,
                packagePage.getNumber(),
                packagePage.getSize(),
                packagePage.getTotalElements(),
                packagePage.getTotalPages(),
                packagePage.isFirst(),
                packagePage.isLast(),
                resolvedSortBy,
                sortDirection.name()
        );
    }

    @Transactional
    public void deletePackage(Long id) {
        TourPackage tourPackage = findPackage(id);
        tourPackage.getGuides().clear();
        tourPackageRepository.delete(tourPackage);
    }

    public TourPackageSummary toSummary(TourPackage tourPackage) {
        return new TourPackageSummary(
                tourPackage.getId(),
                tourPackage.getPackageCode(),
                tourPackage.getTitle(),
                tourPackage.getPrice(),
                tourPackage.getDurationInDays()
        );
    }

    public TourPackage findPackage(Long id) {
        return tourPackageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour package not found."));
    }

    private Set<TourGuide> resolveGuides(Set<Long> guideIds) {
        if (guideIds == null || guideIds.isEmpty()) {
            return Set.of();
        }

        List<TourGuide> guides = tourGuideRepository.findAllById(guideIds);
        if (guides.size() != guideIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more guide IDs were not found.");
        }
        return new LinkedHashSet<>(guides);
    }

    private TourPackageResponse toResponse(TourPackage tourPackage) {
        List<TourGuideResponse> guides = new ArrayList<>();
        for (TourGuide guide : tourPackage.getGuides()) {
            guides.add(tourGuideService.toResponse(guide));
        }

        return new TourPackageResponse(
                tourPackage.getId(),
                tourPackage.getPackageCode(),
                tourPackage.getTitle(),
                tourPackage.getDescription(),
                tourPackage.getPrice(),
                tourPackage.getDurationInDays(),
                guides
        );
    }

    private void applyPackageChanges(TourPackage tourPackage, TourPackageRequest request) {
        tourPackage.setPackageCode(request.packageCode().trim());
        tourPackage.setTitle(request.title().trim());
        tourPackage.setDescription(request.description().trim());
        tourPackage.setPrice(request.price());
        tourPackage.setDurationInDays(request.durationInDays());
        tourPackage.getGuides().clear();
        tourPackage.getGuides().addAll(resolveGuides(request.guideIds()));
    }

    private void applyPackagePatch(TourPackage tourPackage, TourPackagePatchRequest request) {
        if (request.packageCode() != null) {
            tourPackage.setPackageCode(normalizeRequiredValue(request.packageCode(), "packageCode"));
        }
        if (request.title() != null) {
            tourPackage.setTitle(normalizeRequiredValue(request.title(), "title"));
        }
        if (request.description() != null) {
            tourPackage.setDescription(normalizeRequiredValue(request.description(), "description"));
        }
        if (request.price() != null) {
            tourPackage.setPrice(request.price());
        }
        if (request.durationInDays() != null) {
            tourPackage.setDurationInDays(request.durationInDays());
        }
        if (request.guideIds() != null) {
            tourPackage.getGuides().clear();
            tourPackage.getGuides().addAll(resolveGuides(request.guideIds()));
        }
    }

    private void validateUniquePackageFields(String packageCode, String title, Long currentPackageId) {
        if (tourPackageRepository.existsByPackageCodeIgnoreCase(packageCode)) {
            TourPackage packageWithCode = tourPackageRepository.findByPackageCodeIgnoreCase(packageCode);
            if (packageWithCode != null && !packageWithCode.getId().equals(currentPackageId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Package code already exists.");
            }
        }

        if (tourPackageRepository.existsByTitleIgnoreCase(title)) {
            TourPackage packageWithTitle = tourPackageRepository.findByTitleIgnoreCase(title);
            if (packageWithTitle != null && !packageWithTitle.getId().equals(currentPackageId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Package title already exists.");
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

    private String resolveSortField(String sortBy, Set<String> allowedFields, String defaultField) {
        if (!StringUtils.hasText(sortBy)) {
            return defaultField;
        }
        if (!allowedFields.contains(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported sort field: " + sortBy);
        }
        return sortBy;
    }

    private Sort.Direction resolveDirection(String direction) {
        if (!StringUtils.hasText(direction)) {
            return Sort.Direction.ASC;
        }
        try {
            return Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported sort direction: " + direction);
        }
    }
}

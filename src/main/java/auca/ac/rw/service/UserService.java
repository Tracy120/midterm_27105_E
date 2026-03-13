package auca.ac.rw.service;

import auca.ac.rw.dto.LocationHierarchyResponse;
import auca.ac.rw.dto.PageResponse;
import auca.ac.rw.dto.UserPatchRequest;
import auca.ac.rw.dto.UserBookingSummary;
import auca.ac.rw.dto.UserProfilePatchRequest;
import auca.ac.rw.dto.UserProfileRequest;
import auca.ac.rw.dto.UserProfileResponse;
import auca.ac.rw.dto.UserRequest;
import auca.ac.rw.dto.UserResponse;
import auca.ac.rw.entity.AppUser;
import auca.ac.rw.entity.Cell;
import auca.ac.rw.entity.District;
import auca.ac.rw.entity.Province;
import auca.ac.rw.entity.Sector;
import auca.ac.rw.entity.UserProfile;
import auca.ac.rw.entity.Village;
import auca.ac.rw.repository.AppUserRepository;
import auca.ac.rw.repository.VillageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserService {

    private static final Set<String> USER_SORT_FIELDS = Set.of("firstName", "lastName", "email", "createdAt");

    private final AppUserRepository appUserRepository;
    private final VillageRepository villageRepository;

    public UserService(AppUserRepository appUserRepository, VillageRepository villageRepository) {
        this.appUserRepository = appUserRepository;
        this.villageRepository = villageRepository;
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        validateUniqueUserFields(request.userCode().trim(), request.email().trim(), null);
        AppUser user = new AppUser();
        user.setCreatedAt(LocalDateTime.now());
        applyUserChanges(user, request);
        return toUserResponse(appUserRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        AppUser user = findUser(id);
        validateUniqueUserFields(request.userCode().trim(), request.email().trim(), user.getId());
        applyUserChanges(user, request);
        return toUserResponse(appUserRepository.save(user));
    }

    @Transactional
    public UserResponse patchUser(Long id, UserPatchRequest request) {
        AppUser user = findUser(id);
        String updatedUserCode = resolveRequiredPatchValue(user.getUserCode(), request.userCode(), "userCode");
        String updatedEmail = resolveRequiredPatchValue(user.getEmail(), request.email(), "email");
        validateUniqueUserFields(updatedUserCode, updatedEmail, user.getId());
        applyUserPatch(user, request);
        return toUserResponse(appUserRepository.save(user));
    }

    public UserResponse getUser(Long id) {
        return toUserResponse(findUser(id));
    }

    public PageResponse<UserResponse> getUsers(int page, int size, String sortBy, String direction) {
        String resolvedSortBy = resolveSortField(sortBy, USER_SORT_FIELDS, "createdAt");
        Sort.Direction sortDirection = resolveDirection(direction);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, resolvedSortBy));
        Page<AppUser> userPage = appUserRepository.findAll(pageRequest);
        List<UserResponse> users = new ArrayList<>();

        for (AppUser user : userPage.getContent()) {
            users.add(toUserResponse(user));
        }

        return new PageResponse<>(
                users,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast(),
                resolvedSortBy,
                sortDirection.name()
        );
    }

    public List<UserResponse> getUsersByProvince(String provinceCode, String provinceName) {
        List<AppUser> users;

        if (StringUtils.hasText(provinceCode)) {
            users = appUserRepository.findAllByProvinceCode(provinceCode.trim());
        } else if (StringUtils.hasText(provinceName)) {
            users = appUserRepository.findAllByProvinceName(provinceName.trim());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide provinceCode or provinceName.");
        }

        List<UserResponse> responses = new ArrayList<>();
        for (AppUser user : users) {
            responses.add(toUserResponse(user));
        }
        return responses;
    }

    public List<UserResponse> getUsersByLocation(String provinceCode,
                                                 String districtCode,
                                                 String sectorCode,
                                                 String cellCode,
                                                 String villageCode) {
        String normalizedProvinceCode = normalizeFilter(provinceCode);
        String normalizedDistrictCode = normalizeFilter(districtCode);
        String normalizedSectorCode = normalizeFilter(sectorCode);
        String normalizedCellCode = normalizeFilter(cellCode);
        String normalizedVillageCode = normalizeFilter(villageCode);

        if (normalizedProvinceCode == null
                && normalizedDistrictCode == null
                && normalizedSectorCode == null
                && normalizedCellCode == null
                && normalizedVillageCode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide at least one location filter such as provinceCode, districtCode, sectorCode, cellCode, or villageCode.");
        }

        List<AppUser> users = appUserRepository.findAllByLocation(
                normalizedProvinceCode,
                normalizedDistrictCode,
                normalizedSectorCode,
                normalizedCellCode,
                normalizedVillageCode
        );

        List<UserResponse> responses = new ArrayList<>();
        for (AppUser user : users) {
            responses.add(toUserResponse(user));
        }
        return responses;
    }

    @Transactional
    public void deleteUser(Long id) {
        appUserRepository.delete(findUser(id));
    }

    public UserBookingSummary toBookingSummary(AppUser user) {
        Village village = user.getVillage();
        Province province = village.getProvince();

        return new UserBookingSummary(
                user.getId(),
                user.getUserCode(),
                user.getFullName(),
                user.getEmail(),
                village.getName(),
                province.getName()
        );
    }

    public AppUser findUser(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    private Village resolveVillage(String villageCode, String villageName) {
        if (StringUtils.hasText(villageCode)) {
            return villageRepository.findByCodeIgnoreCase(villageCode.trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Village code not found."));
        }

        if (StringUtils.hasText(villageName)) {
            List<Village> villages = villageRepository.findByNameIgnoreCase(villageName.trim());
            if (villages.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Village name not found.");
            }
            if (villages.size() > 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Village name is ambiguous. Use villageCode.");
            }
            return villages.get(0);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must be linked using villageCode or villageName.");
    }

    private void applyUserChanges(AppUser user, UserRequest request) {
        user.setUserCode(request.userCode().trim());
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(request.email().trim());
        user.setPhoneNumber(request.phoneNumber().trim());
        user.setVillage(resolveVillage(request.villageCode(), request.villageName()));
        updateProfile(user, request.profile());
    }

    private void applyUserPatch(AppUser user, UserPatchRequest request) {
        if (request.userCode() != null) {
            user.setUserCode(normalizeRequiredValue(request.userCode(), "userCode"));
        }
        if (request.firstName() != null) {
            user.setFirstName(normalizeRequiredValue(request.firstName(), "firstName"));
        }
        if (request.lastName() != null) {
            user.setLastName(normalizeRequiredValue(request.lastName(), "lastName"));
        }
        if (request.email() != null) {
            user.setEmail(normalizeRequiredValue(request.email(), "email"));
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(normalizeRequiredValue(request.phoneNumber(), "phoneNumber"));
        }
        if (request.villageCode() != null || request.villageName() != null) {
            user.setVillage(resolveVillage(request.villageCode(), request.villageName()));
        }
        patchProfile(user, request.profile());
    }

    private void updateProfile(AppUser user, UserProfileRequest profileRequest) {
        if (profileRequest == null) {
            user.setProfile(null);
            return;
        }

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
        }
        profile.setNationalId(profileRequest.nationalId());
        profile.setGender(profileRequest.gender());
        profile.setEmergencyContact(profileRequest.emergencyContact());
        profile.setPassportNumber(profileRequest.passportNumber());
        user.setProfile(profile);
    }

    private void patchProfile(AppUser user, UserProfilePatchRequest profileRequest) {
        if (profileRequest == null) {
            return;
        }

        boolean hasChanges = profileRequest.nationalId() != null
                || profileRequest.gender() != null
                || profileRequest.emergencyContact() != null
                || profileRequest.passportNumber() != null;

        if (!hasChanges) {
            return;
        }

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            user.setProfile(profile);
        }

        if (profileRequest.nationalId() != null) {
            profile.setNationalId(normalizeOptionalValue(profileRequest.nationalId()));
        }
        if (profileRequest.gender() != null) {
            profile.setGender(normalizeOptionalValue(profileRequest.gender()));
        }
        if (profileRequest.emergencyContact() != null) {
            profile.setEmergencyContact(normalizeOptionalValue(profileRequest.emergencyContact()));
        }
        if (profileRequest.passportNumber() != null) {
            profile.setPassportNumber(normalizeOptionalValue(profileRequest.passportNumber()));
        }
    }

    private void validateUniqueUserFields(String userCode, String email, Long currentUserId) {
        if (appUserRepository.existsByUserCodeIgnoreCase(userCode)) {
            AppUser userWithCode = appUserRepository.findByUserCodeIgnoreCase(userCode);
            if (userWithCode != null && !userWithCode.getId().equals(currentUserId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User code already exists.");
            }
        }

        if (appUserRepository.existsByEmailIgnoreCase(email)) {
            AppUser userWithEmail = appUserRepository.findByEmailIgnoreCase(email);
            if (userWithEmail != null && !userWithEmail.getId().equals(currentUserId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists.");
            }
        }
    }

    private UserResponse toUserResponse(AppUser user) {
        Village village = user.getVillage();
        Cell cell = village.getCell();
        Sector sector = village.getSector();
        District district = village.getDistrict();
        Province province = village.getProvince();
        LocationHierarchyResponse location = new LocationHierarchyResponse(
                province.getCode(),
                province.getName(),
                district.getCode(),
                district.getName(),
                sector.getCode(),
                sector.getName(),
                cell.getCode(),
                cell.getName(),
                village.getCode(),
                village.getName()
        );

        return new UserResponse(
                user.getId(),
                user.getUserCode(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                location,
                toProfileResponse(user.getProfile()),
                user.getCreatedAt()
        );
    }

    private UserProfileResponse toProfileResponse(UserProfile profile) {
        if (profile == null) {
            return null;
        }

        return new UserProfileResponse(
                profile.getId(),
                profile.getNationalId(),
                profile.getGender(),
                profile.getEmergencyContact(),
                profile.getPassportNumber()
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

    private String normalizeOptionalValue(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeFilter(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
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

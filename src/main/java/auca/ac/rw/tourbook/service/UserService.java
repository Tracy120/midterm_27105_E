package auca.ac.rw.tourbook.service;

import auca.ac.rw.tourbook.model.Location;
import auca.ac.rw.tourbook.model.LocationType;
import auca.ac.rw.tourbook.model.User;
import auca.ac.rw.tourbook.repository.LocationRepository;
import auca.ac.rw.tourbook.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public UserService(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public String registerUser(User user) {
        if (user == null) {
            return "Error: User payload is required.";
        }
        if (!StringUtils.hasText(user.getUserCode())) {
            return "Error: User code is required.";
        }
        if (!StringUtils.hasText(user.getEmail())) {
            return "Error: Email is required.";
        }
        if (userRepository.existsByUserCodeIgnoreCase(user.getUserCode().trim())) {
            return "Error: User code already exists.";
        }
        if (userRepository.existsByEmailIgnoreCase(user.getEmail().trim())) {
            return "Error: Email already exists.";
        }

        Object villageResolution = resolveVillage(user.getVillageCode(), user.getVillageName());
        if (villageResolution instanceof String) {
            return villageResolution.toString();
        }

        normalizeUser(user);
        user.setLocation((Location) villageResolution);
        userRepository.save(user);
        return "User registered successfully.";
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getUsersByProvinceName(String provinceName) {
        if (!StringUtils.hasText(provinceName)) {
            return Collections.emptyList();
        }
        return userRepository.findUsersByProvinceName(provinceName.trim());
    }

    public List<User> getUsersByProvinceCode(String provinceCode) {
        if (!StringUtils.hasText(provinceCode)) {
            return Collections.emptyList();
        }
        return userRepository.findUsersByProvinceCode(provinceCode.trim());
    }

    public User updateUser(Long id, User newUserData) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            return null;
        }
        if (newUserData == null) {
            return null;
        }
        if (!StringUtils.hasText(newUserData.getUserCode())) {
            return null;
        }
        if (!StringUtils.hasText(newUserData.getEmail())) {
            return null;
        }

        User existingUser = existingUserOpt.get();
        normalizeUser(newUserData);

        User userWithSameCode = userRepository.findByUserCodeIgnoreCase(newUserData.getUserCode());
        if (userWithSameCode != null && !userWithSameCode.getId().equals(existingUser.getId())) {
            return null;
        }

        User userWithSameEmail = userRepository.findByEmailIgnoreCase(newUserData.getEmail());
        if (userWithSameEmail != null && !userWithSameEmail.getId().equals(existingUser.getId())) {
            return null;
        }

        existingUser.setUserCode(newUserData.getUserCode().trim());
        existingUser.setFirstName(newUserData.getFirstName().trim());
        existingUser.setLastName(newUserData.getLastName().trim());
        existingUser.setEmail(newUserData.getEmail().trim());
        existingUser.setPhoneNumber(newUserData.getPhoneNumber().trim());

        if (StringUtils.hasText(newUserData.getVillageCode()) || StringUtils.hasText(newUserData.getVillageName())) {
            Object villageResolution = resolveVillage(newUserData.getVillageCode(), newUserData.getVillageName());
            if (villageResolution instanceof String) {
                return null;
            }
            existingUser.setLocation((Location) villageResolution);
        }

        return userRepository.save(existingUser);
    }

    public String deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return "Error: User not found.";
        }
        userRepository.deleteById(id);
        return "Success: User deleted.";
    }

    public Page<User> getAllUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findAll(pageable);
    }

    public List<User> getUsersByLocation(String provinceCode,
                                         String districtCode,
                                         String sectorCode,
                                         String cellCode,
                                         String villageCode) {
        if (!StringUtils.hasText(provinceCode)
                && !StringUtils.hasText(districtCode)
                && !StringUtils.hasText(sectorCode)
                && !StringUtils.hasText(cellCode)
                && !StringUtils.hasText(villageCode)) {
            return Collections.emptyList();
        }

        return userRepository.findAll().stream()
                .filter(user -> matchesLocation(user.getLocation(), provinceCode, districtCode, sectorCode, cellCode, villageCode))
                .sorted(Comparator.comparing(User::getFirstName).thenComparing(User::getLastName))
                .collect(Collectors.toList());
    }

    public User findUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    private void normalizeUser(User user) {
        if (StringUtils.hasText(user.getUserCode())) {
            user.setUserCode(user.getUserCode().trim());
        }
        if (StringUtils.hasText(user.getFirstName())) {
            user.setFirstName(user.getFirstName().trim());
        }
        if (StringUtils.hasText(user.getLastName())) {
            user.setLastName(user.getLastName().trim());
        }
        if (StringUtils.hasText(user.getEmail())) {
            user.setEmail(user.getEmail().trim());
        }
        if (StringUtils.hasText(user.getPhoneNumber())) {
            user.setPhoneNumber(user.getPhoneNumber().trim());
        }
    }

    private Object resolveVillage(String villageCode, String villageName) {
        if (StringUtils.hasText(villageCode)) {
            return locationRepository.findByCodeIgnoreCaseAndType(villageCode.trim(), LocationType.VILLAGE)
                    .map(location -> (Object) location)
                    .orElse("Error: Village code not found.");
        }

        if (StringUtils.hasText(villageName)) {
            List<Location> villages = locationRepository.findByNameIgnoreCaseAndType(villageName.trim(), LocationType.VILLAGE);
            if (villages.size() == 1) {
                return villages.get(0);
            }
            if (villages.size() > 1) {
                return "Error: Village name is ambiguous. Use village code.";
            }
            return "Error: Village name not found.";
        }

        return "Error: villageCode or villageName is required.";
    }

    private boolean matchesLocation(Location village,
                                    String provinceCode,
                                    String districtCode,
                                    String sectorCode,
                                    String cellCode,
                                    String villageCode) {
        if (village == null || village.getType() != LocationType.VILLAGE) {
            return false;
        }

        Location cell = village.getParent();
        Location sector = cell == null ? null : cell.getParent();
        Location district = sector == null ? null : sector.getParent();
        Location province = district == null ? null : district.getParent();

        return matchesCode(province, provinceCode)
                && matchesCode(district, districtCode)
                && matchesCode(sector, sectorCode)
                && matchesCode(cell, cellCode)
                && matchesCode(village, villageCode);
    }

    private boolean matchesCode(Location location, String code) {
        if (!StringUtils.hasText(code)) {
            return true;
        }
        return location != null && location.getCode() != null && location.getCode().equalsIgnoreCase(code.trim());
    }
}

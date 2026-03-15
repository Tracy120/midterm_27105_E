package auca.ac.rw.tourbook.service;

import auca.ac.rw.tourbook.model.Location;
import auca.ac.rw.tourbook.model.LocationType;
import auca.ac.rw.tourbook.repository.LocationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Object saveChildLocation(Location location, Long parentId) {
        String validationError = validateLocation(location, null);
        if (validationError != null) {
            return validationError;
        }

        if (parentId != null) {
            Object parentResolution = resolveParent(location.getType(), parentId);
            if (parentResolution instanceof String) {
                return parentResolution;
            }
            location.setParent((Location) parentResolution);
        } else if (location.getType() != LocationType.PROVINCE) {
            return "Error: Parent location is required.";
        }

        return locationRepository.save(location);
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll(Sort.by("name"));
    }

    public List<Location> getAllLocationsByProvince(String provinceName) {
        if (!StringUtils.hasText(provinceName)) {
            return Collections.emptyList();
        }

        List<Location> locations = locationRepository.findAll();
        List<Location> results = new ArrayList<>();

        for (Location location : locations) {
            if (provinceName.trim().equalsIgnoreCase(resolveProvinceName(location))) {
                results.add(location);
            }
        }

        return results;
    }

    public List<Location> getLocationsByType(LocationType type) {
        return locationRepository.findByTypeOrderByNameAsc(type);
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public Object updateLocation(Long id, Location newLocationData, Long parentId) {
        Optional<Location> existingLocationOpt = locationRepository.findById(id);
        if (!existingLocationOpt.isPresent()) {
            return "Error: Location not found.";
        }

        String validationError = validateLocation(newLocationData, id);
        if (validationError != null) {
            return validationError;
        }

        Location existingLocation = existingLocationOpt.get();
        existingLocation.setCode(newLocationData.getCode().trim());
        existingLocation.setName(newLocationData.getName().trim());
        existingLocation.setType(newLocationData.getType());

        if (parentId != null) {
            Object parentResolution = resolveParent(existingLocation.getType(), parentId);
            if (parentResolution instanceof String) {
                return parentResolution;
            }
            existingLocation.setParent((Location) parentResolution);
        } else if (existingLocation.getType() == LocationType.PROVINCE) {
            existingLocation.setParent(null);
        } else {
            return "Error: Parent location is required.";
        }

        return locationRepository.save(existingLocation);
    }

    public String deleteLocation(Long id) {
        Optional<Location> locationOpt = locationRepository.findById(id);
        if (!locationOpt.isPresent()) {
            return "Error: Location not found.";
        }

        Location location = locationOpt.get();
        if (!location.getChildren().isEmpty()) {
            return "Error: Delete child locations first.";
        }
        if (!location.getUsers().isEmpty()) {
            return "Error: This location is linked to users.";
        }

        locationRepository.delete(location);
        return "Success: Location deleted.";
    }

    private Location resolveProvince(Location location) {
        Location current = location;
        while (current != null && current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }

    private String validateLocation(Location location, Long currentId) {
        if (location == null) {
            return "Error: Location payload is required.";
        }
        if (!StringUtils.hasText(location.getCode())) {
            return "Error: Location code is required.";
        }
        if (!StringUtils.hasText(location.getName())) {
            return "Error: Location name is required.";
        }
        if (location.getType() == null) {
            return "Error: Location type is required.";
        }

        String normalizedCode = location.getCode().trim();
        Optional<Location> existingLocationOpt = locationRepository.findByCodeIgnoreCase(normalizedCode);
        if (existingLocationOpt.isPresent() && !existingLocationOpt.get().getId().equals(currentId)) {
            return "Error: Location code already exists.";
        }

        location.setCode(normalizedCode);
        location.setName(location.getName().trim());
        return null;
    }

    private Object resolveParent(LocationType type, Long parentId) {
        Optional<Location> parentOpt = locationRepository.findById(parentId);
        if (!parentOpt.isPresent()) {
            return "Error: Parent location not found.";
        }

        Location parent = parentOpt.get();
        if (type == LocationType.PROVINCE) {
            return "Error: Province cannot have a parent.";
        }

        LocationType expectedParentType = getExpectedParentType(type);
        if (parent.getType() != expectedParentType) {
            return buildParentTypeError(type, expectedParentType);
        }
        return parent;
    }

    private String resolveProvinceName(Location location) {
        Location province = resolveProvince(location);
        if (province == null) {
            return "";
        }
        return province.getName().trim();
    }

    private LocationType getExpectedParentType(LocationType type) {
        if (type == LocationType.DISTRICT) {
            return LocationType.PROVINCE;
        }
        if (type == LocationType.SECTOR) {
            return LocationType.DISTRICT;
        }
        if (type == LocationType.CELL) {
            return LocationType.SECTOR;
        }
        if (type == LocationType.VILLAGE) {
            return LocationType.CELL;
        }
        return null;
    }

    private String buildParentTypeError(LocationType type, LocationType expectedParentType) {
        StringBuilder message = new StringBuilder("Error: ");
        message.append(type);
        message.append(" must belong to a ");
        message.append(expectedParentType);
        message.append('.');
        return message.toString();
    }
}

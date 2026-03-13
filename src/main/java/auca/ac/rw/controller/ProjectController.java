package auca.ac.rw.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ProjectController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "project", "Tour Booking System with Rwanda Administrative Location Hierarchy",
                "focus", "Users are linked to villages, and province queries traverse the full hierarchy.",
                "dataMode", "No records are preloaded. Create your own test data through the CRUD endpoints.",
                "mainEndpoints", List.of(
                        "/api/locations/provinces",
                        "/api/locations/districts",
                        "/api/locations/sectors",
                        "/api/locations/cells",
                        "/api/locations/villages",
                        "/api/users",
                        "/api/users/by-province",
                        "/api/users/by-location",
                        "/api/guides",
                        "/api/packages",
                        "/api/bookings"
                )
        );
    }
}

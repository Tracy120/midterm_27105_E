package auca.ac.rw.dto;

public record DemoResetResponse(
        String message,
        long deletedBookings,
        long deletedPackages,
        long deletedGuides,
        long deletedProfiles,
        long deletedUsers,
        long deletedVillages,
        long deletedCells,
        long deletedSectors,
        long deletedDistricts,
        long deletedProvinces
) {
}

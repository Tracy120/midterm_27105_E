package auca.ac.rw.dto;

public record DemoSeedResponse(
        String message,
        Long provinceId,
        Long districtId,
        Long sectorId,
        Long cellId,
        Long villageId,
        Long guide1Id,
        Long guide2Id,
        Long guide3Id,
        Long user1Id,
        Long user2Id,
        Long user3Id,
        Long package1Id,
        Long package2Id,
        Long package3Id
) {
}

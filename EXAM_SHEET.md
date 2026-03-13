# Exam Cheat Sheet

Base URL:

```text
http://localhost:8080
```

Start the API:

```powershell
mvn spring-boot:run
```

Use `Content-Type: application/json` for all `POST`, `PUT`, and `PATCH` requests.

## 1. Create the location hierarchy first

### POST province

`POST http://localhost:8080/api/locations/provinces`

```json
{
  "code": "PRV-901",
  "name": "Northern Province Demo"
}
```

Save returned `provinceId`.

### POST district

`POST http://localhost:8080/api/locations/districts`

```json
{
  "code": "DST-901",
  "name": "Gicumbi Demo",
  "provinceCode": "PRV-901"
}
```

Save returned `districtId`.

### POST sector

`POST http://localhost:8080/api/locations/sectors`

```json
{
  "code": "SEC-901",
  "name": "Byumba Demo",
  "districtCode": "DST-901"
}
```

Save returned `sectorId`.

### POST cell

`POST http://localhost:8080/api/locations/cells`

```json
{
  "code": "CEL-901",
  "name": "Ngondore Demo",
  "sectorCode": "SEC-901"
}
```

Save returned `cellId`.

### POST village

`POST http://localhost:8080/api/locations/villages`

```json
{
  "code": "VLG-901",
  "name": "Kabeza Demo",
  "cellCode": "CEL-901"
}
```

Save returned `villageId`.

## 2. Quick GET checks for locations

`GET http://localhost:8080/api/locations/provinces`

`GET http://localhost:8080/api/locations/districts?provinceCode=PRV-901`

`GET http://localhost:8080/api/locations/sectors?districtCode=DST-901`

`GET http://localhost:8080/api/locations/cells?sectorCode=SEC-901`

`GET http://localhost:8080/api/locations/villages?cellCode=CEL-901`

`GET http://localhost:8080/api/locations/villages/{villageId}`

## 3. Create 3 guides

### Guide 1

`POST http://localhost:8080/api/guides`

```json
{
  "guideCode": "GDE-901",
  "fullName": "Brenda Uwera",
  "email": "brenda.uwera@example.com",
  "phoneNumber": "0788000901",
  "specialization": "Bird watching"
}
```

Save returned `guide1Id`.

### Guide 2

`POST http://localhost:8080/api/guides`

```json
{
  "guideCode": "GDE-902",
  "fullName": "Kevin Mutoni",
  "email": "kevin.mutoni@example.com",
  "phoneNumber": "0788000902",
  "specialization": "Cultural tourism"
}
```

Save returned `guide2Id`.

### Guide 3

`POST http://localhost:8080/api/guides`

```json
{
  "guideCode": "GDE-903",
  "fullName": "Sonia Ingabire",
  "email": "sonia.ingabire@example.com",
  "phoneNumber": "0788000903",
  "specialization": "Hiking"
}
```

Save returned `guide3Id`.

### GET guides

`GET http://localhost:8080/api/guides`

## 4. Create 3 users

### User 1

`POST http://localhost:8080/api/users`

```json
{
  "userCode": "USR-901",
  "firstName": "Lionel",
  "lastName": "Hategekimana",
  "email": "lionel901@example.com",
  "phoneNumber": "0788000911",
  "villageCode": "VLG-901",
  "profile": {
    "nationalId": "1199988877769901",
    "gender": "MALE",
    "emergencyContact": "0788777901",
    "passportNumber": "P9901"
  }
}
```

Save returned `user1Id`.

### User 2

`POST http://localhost:8080/api/users`

```json
{
  "userCode": "USR-902",
  "firstName": "Ariane",
  "lastName": "Uwimana",
  "email": "ariane902@example.com",
  "phoneNumber": "0788000912",
  "villageCode": "VLG-901",
  "profile": {
    "nationalId": "1199988877769902",
    "gender": "FEMALE",
    "emergencyContact": "0788777902",
    "passportNumber": "P9902"
  }
}
```

Save returned `user2Id`.

### User 3

`POST http://localhost:8080/api/users`

```json
{
  "userCode": "USR-903",
  "firstName": "David",
  "lastName": "Nshimiyimana",
  "email": "david903@example.com",
  "phoneNumber": "0788000913",
  "villageCode": "VLG-901",
  "profile": {
    "nationalId": "1199988877769903",
    "gender": "MALE",
    "emergencyContact": "0788777903",
    "passportNumber": "P9903"
  }
}
```

Save returned `user3Id`.

### GET users

`GET http://localhost:8080/api/users`

`GET http://localhost:8080/api/users/{user1Id}`

`GET http://localhost:8080/api/users/by-province?provinceCode=PRV-901`

## 5. Create 3 packages

Replace `guideIds` with your real guide IDs.

### Package 1

`POST http://localhost:8080/api/packages`

```json
{
  "packageCode": "PKG-901",
  "title": "Volcano Ridge Trek",
  "description": "Two-day mountain trek with sunrise views.",
  "price": 540,
  "durationInDays": 2,
  "guideIds": [1]
}
```

Save returned `package1Id`.

### Package 2

`POST http://localhost:8080/api/packages`

```json
{
  "packageCode": "PKG-902",
  "title": "Kigali Heritage Walk",
  "description": "Museum, city market, and food experience.",
  "price": 120,
  "durationInDays": 1,
  "guideIds": [2]
}
```

Save returned `package2Id`.

### Package 3

`POST http://localhost:8080/api/packages`

```json
{
  "packageCode": "PKG-903",
  "title": "Lake Kivu Weekend",
  "description": "Boat ride and lakeside rest.",
  "price": 320,
  "durationInDays": 2,
  "guideIds": [3]
}
```

Save returned `package3Id`.

### GET packages

`GET http://localhost:8080/api/packages`

`GET http://localhost:8080/api/packages?page=0&size=5&sortBy=price&direction=desc`

`GET http://localhost:8080/api/packages/{package1Id}`

## 6. Create 3 bookings

Replace `userId` and `tourPackageId` with the real IDs returned above.

### Booking 1

`POST http://localhost:8080/api/bookings`

```json
{
  "bookingReference": "BKG-901",
  "userId": 1,
  "tourPackageId": 1,
  "travelDate": "2026-03-25",
  "numberOfPeople": 3,
  "status": "PENDING"
}
```

Save returned `booking1Id`.

### Booking 2

`POST http://localhost:8080/api/bookings`

```json
{
  "bookingReference": "BKG-902",
  "userId": 2,
  "tourPackageId": 2,
  "travelDate": "2026-03-27",
  "numberOfPeople": 2,
  "status": "CONFIRMED"
}
```

Save returned `booking2Id`.

### Booking 3

`POST http://localhost:8080/api/bookings`

```json
{
  "bookingReference": "BKG-903",
  "userId": 3,
  "tourPackageId": 3,
  "travelDate": "2026-03-30",
  "numberOfPeople": 1,
  "status": "CANCELLED"
}
```

Save returned `booking3Id`.

### GET bookings

`GET http://localhost:8080/api/bookings`

`GET http://localhost:8080/api/bookings?page=0&size=5&sortBy=bookingDate&direction=desc`

`GET http://localhost:8080/api/bookings/{booking1Id}`

## 7. Show PUT in the exam

### PUT user

`PUT http://localhost:8080/api/users/{user1Id}`

```json
{
  "userCode": "USR-901",
  "firstName": "Lionel",
  "lastName": "Hategekimana",
  "email": "lionel901@example.com",
  "phoneNumber": "0788999911",
  "villageCode": "VLG-901",
  "profile": {
    "nationalId": "1199988877769901",
    "gender": "MALE",
    "emergencyContact": "0788777991",
    "passportNumber": "P9901"
  }
}
```

### PUT package

`PUT http://localhost:8080/api/packages/{package1Id}`

```json
{
  "packageCode": "PKG-901",
  "title": "Volcano Ridge Trek",
  "description": "Updated trek with campsite dinner.",
  "price": 580,
  "durationInDays": 3,
  "guideIds": [1, 2]
}
```

### PUT village

`PUT http://localhost:8080/api/locations/villages/{villageId}`

```json
{
  "code": "VLG-901",
  "name": "Kabeza Updated",
  "cellCode": "CEL-901"
}
```

## 8. Show PATCH in the exam

### PATCH guide

`PATCH http://localhost:8080/api/guides/{guide1Id}`

```json
{
  "specialization": "Community tourism"
}
```

### PATCH user

`PATCH http://localhost:8080/api/users/{user1Id}`

```json
{
  "email": "lionel901.updated@example.com",
  "phoneNumber": "0788000999",
  "profile": {
    "emergencyContact": "0788777999"
  }
}
```

### PATCH booking

`PATCH http://localhost:8080/api/bookings/{booking1Id}`

```json
{
  "numberOfPeople": 5,
  "status": "CANCELLED"
}
```

### PATCH province

`PATCH http://localhost:8080/api/locations/provinces/{provinceId}`

```json
{
  "name": "Northern Province Patched"
}
```

## 9. Show DELETE in the exam

Delete in this order:
DELETE http://localhost:8080/api/packages/4
DELETE http://localhost:8080/api/packages/3
DELETE http://localhost:8080/api/packages/2

DELETE http://localhost:8080/api/users/8
DELETE http://localhost:8080/api/users/7
DELETE http://localhost:8080/api/users/6

DELETE http://localhost:8080/api/guides/4
DELETE http://localhost:8080/api/guides/3
DELETE http://localhost:8080/api/guides/2

DELETE http://localhost:8080/api/locations/villages/2
DELETE http://localhost:8080/api/locations/cells/2
DELETE http://localhost:8080/api/locations/sectors/2
DELETE http://localhost:8080/api/locations/districts/2
DELETE http://localhost:8080/api/locations/provinces/2


## 10. Success codes to mention

- `POST` -> `201 Created`
- `GET` -> `200 OK`
- `PUT` -> `200 OK`
- `PATCH` -> `200 OK`
- `DELETE` -> `204 No Content`

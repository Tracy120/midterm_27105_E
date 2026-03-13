# Tour Booking System with Rwanda Administrative Location Hierarchy

This project is a Spring Boot REST API for a tourism-themed exam. It combines tourism management with Rwanda's administrative location hierarchy so that users are saved through `Village`, not directly through `Province`.

## Exam Requirements Covered

- Rwanda hierarchy: `Province -> District -> Sector -> Cell -> Village`
- One-to-many: location chain, `User -> Booking`, `TourPackage -> Booking`
- One-to-one: `AppUser -> UserProfile`
- Many-to-many: `TourPackage <-> TourGuide`
- Duplicate checks with `existsBy...` and uniqueness validation
- Retrieve users by `provinceCode` or `provinceName`
- Pagination and sorting for tour packages, users, and bookings

## Database Configuration

The default application configuration points to your PostgreSQL database:

- Host: `localhost`
- Port: `5433`
- Database: `exam`
- Username: `postgres`

Set the password before running if needed:

```powershell
$env:DB_PASSWORD="your_password"
mvn spring-boot:run
```

You can also override the full connection:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5433/exam"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
mvn spring-boot:run
```

## Main Endpoints

### Location setup

- `POST /api/locations/provinces`
- `GET /api/locations/provinces`
- `GET /api/locations/provinces/{id}`
- `PUT /api/locations/provinces/{id}`
- `PATCH /api/locations/provinces/{id}`
- `DELETE /api/locations/provinces/{id}`
- `POST /api/locations/districts`
- `GET /api/locations/districts`
- `GET /api/locations/districts/{id}`
- `PUT /api/locations/districts/{id}`
- `PATCH /api/locations/districts/{id}`
- `DELETE /api/locations/districts/{id}`
- `POST /api/locations/sectors`
- `GET /api/locations/sectors`
- `GET /api/locations/sectors/{id}`
- `PUT /api/locations/sectors/{id}`
- `PATCH /api/locations/sectors/{id}`
- `DELETE /api/locations/sectors/{id}`
- `POST /api/locations/cells`
- `GET /api/locations/cells`
- `GET /api/locations/cells/{id}`
- `PUT /api/locations/cells/{id}`
- `PATCH /api/locations/cells/{id}`
- `DELETE /api/locations/cells/{id}`
- `POST /api/locations/villages`
- `GET /api/locations/villages`
- `GET /api/locations/villages/{id}`
- `PUT /api/locations/villages/{id}`
- `PATCH /api/locations/villages/{id}`
- `DELETE /api/locations/villages/{id}`

Create locations in that order so users can be attached to a village.

### Users

- `POST /api/users`
- `PUT /api/users/{id}`
- `PATCH /api/users/{id}`
- `GET /api/users`
- `GET /api/users/{id}`
- `GET /api/users/by-province?provinceCode=PRV-N`
- `GET /api/users/by-province?provinceName=Northern Province`
- `GET /api/users/by-location?cellCode=CEL-001`
- `GET /api/users/by-location?districtCode=DST-001`
- `DELETE /api/users/{id}`

### Tourism entities

- `POST /api/guides`
- `PUT /api/guides/{id}`
- `PATCH /api/guides/{id}`
- `GET /api/guides`
- `DELETE /api/guides/{id}`
- `POST /api/packages`
- `PUT /api/packages/{id}`
- `PATCH /api/packages/{id}`
- `GET /api/packages?page=0&size=5&sortBy=price&direction=desc`
- `DELETE /api/packages/{id}`
- `POST /api/bookings`
- `PUT /api/bookings/{id}`
- `PATCH /api/bookings/{id}`
- `GET /api/bookings?page=0&size=5&sortBy=bookingDate&direction=desc`
- `DELETE /api/bookings/{id}`

### Demo helpers

- `POST /api/demo/reset`
- `POST /api/demo/reset-and-seed`

Use `reset` before repeating a manual Postman demo so duplicate-code conflicts from older runs do not come back.
Use `reset-and-seed` when you want the API to recreate the sample `901/902/903` location, guide, user, and package records and return their real database IDs.

## Example User Payload

```json
{
  "userCode": "USR-001",
  "firstName": "Aline",
  "lastName": "Uwase",
  "email": "aline@example.com",
  "phoneNumber": "0788000001",
  "villageCode": "VLG-001",
  "profile": {
    "nationalId": "1199988877766655",
    "gender": "FEMALE",
    "emergencyContact": "0788776655",
    "passportNumber": "P12345"
  }
}
```

## Verification

The project was verified with:

```powershell
mvn test
```

The integration tests cover:

- saving a user through village and retrieving that user by province code
- preventing duplicate user email
- pagination and sorting for tour packages
- booking creation linked to both user and tour package
- full `PUT`, `PATCH`, and `DELETE` flows for users, guides, packages, and bookings
- full `POST`, `GET`, `PUT`, `PATCH`, and `DELETE` flows for provinces, districts, sectors, cells, and villages

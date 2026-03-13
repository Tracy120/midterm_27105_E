# Exam Cheat Sheet

Base URL:

```text
http://localhost:8080
```

Start the API:

```powershell
mvn spring-boot:run
```

Important:

- No records are preloaded in the application.
- Create your own data in Postman during the test.
- Save each returned `id` and reuse it in later requests.
- Use `Content-Type: application/json` for `POST`, `PUT`, and `PATCH`.

## 1. Create the location hierarchy first

Create in this order:

1. `POST /api/locations/provinces`
2. `POST /api/locations/districts`
3. `POST /api/locations/sectors`
4. `POST /api/locations/cells`
5. `POST /api/locations/villages`

Example payloads:

```json
{
  "code": "<province-code>",
  "name": "<province-name>"
}
```

```json
{
  "code": "<district-code>",
  "name": "<district-name>",
  "provinceCode": "<province-code>"
}
```

```json
{
  "code": "<sector-code>",
  "name": "<sector-name>",
  "districtCode": "<district-code>"
}
```

```json
{
  "code": "<cell-code>",
  "name": "<cell-name>",
  "sectorCode": "<sector-code>"
}
```

```json
{
  "code": "<village-code>",
  "name": "<village-name>",
  "cellCode": "<cell-code>"
}
```

Quick checks:

- `GET /api/locations/provinces`
- `GET /api/locations/districts?provinceCode=<province-code>`
- `GET /api/locations/sectors?districtCode=<district-code>`
- `GET /api/locations/cells?sectorCode=<sector-code>`
- `GET /api/locations/villages?cellCode=<cell-code>`
- `GET /api/locations/villages/{villageId}`

## 2. Create guides

`POST /api/guides`

```json
{
  "guideCode": "<guide-code>",
  "fullName": "<full-name>",
  "email": "<email>",
  "phoneNumber": "<phone-number>",
  "specialization": "<specialization>"
}
```

Useful checks:

- `GET /api/guides`
- `GET /api/guides/{guideId}`

## 3. Create users

`POST /api/users`

```json
{
  "userCode": "<user-code>",
  "firstName": "<first-name>",
  "lastName": "<last-name>",
  "email": "<email>",
  "phoneNumber": "<phone-number>",
  "villageCode": "<village-code>",
  "profile": {
    "nationalId": "<national-id>",
    "gender": "<gender>",
    "emergencyContact": "<emergency-contact>",
    "passportNumber": "<passport-number>"
  }
}
```

Useful checks:

- `GET /api/users`
- `GET /api/users/{userId}`
- `GET /api/users/by-province?provinceCode=<province-code>`
- `GET /api/users/by-location?villageCode=<village-code>`

## 4. Create packages

`POST /api/packages`

Replace `guideIds` with real guide IDs returned by the API.

```json
{
  "packageCode": "<package-code>",
  "title": "<title>",
  "description": "<description>",
  "price": 250,
  "durationInDays": 2,
  "guideIds": [<guideId1>, <guideId2>]
}
```

Useful checks:

- `GET /api/packages`
- `GET /api/packages?page=0&size=5&sortBy=price&direction=desc`
- `GET /api/packages/{packageId}`

## 5. Create bookings

`POST /api/bookings`

Replace `userId` and `tourPackageId` with real IDs returned earlier.

```json
{
  "bookingReference": "<booking-reference>",
  "userId": <userId>,
  "tourPackageId": <packageId>,
  "travelDate": "<travel-date>",
  "numberOfPeople": 2,
  "status": "PENDING"
}
```

Useful checks:

- `GET /api/bookings`
- `GET /api/bookings?page=0&size=5&sortBy=bookingDate&direction=desc`
- `GET /api/bookings/{bookingId}`

## 6. Show PUT in the exam

Examples:

- `PUT /api/users/{userId}`
- `PUT /api/packages/{packageId}`
- `PUT /api/locations/villages/{villageId}`

For `PUT`, send the full updated object.

## 7. Show PATCH in the exam

Examples:

- `PATCH /api/guides/{guideId}`
- `PATCH /api/users/{userId}`
- `PATCH /api/bookings/{bookingId}`
- `PATCH /api/locations/provinces/{provinceId}`

For `PATCH`, send only the fields you want to change.

## 8. Show DELETE in the exam

Delete in dependency order:

1. bookings
2. packages
3. users
4. guides
5. villages
6. cells
7. sectors
8. districts
9. provinces

Always use the real IDs returned by your own requests.

## 9. Success codes to mention

- `POST` -> `201 Created`
- `GET` -> `200 OK`
- `PUT` -> `200 OK`
- `PATCH` -> `200 OK`
- `DELETE` -> `204 No Content`

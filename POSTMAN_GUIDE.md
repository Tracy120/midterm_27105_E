# Postman Guide For `midterm`

## 1. PostgreSQL Setup
Create a PostgreSQL database called `midterm`.

Example SQL:
```sql
CREATE DATABASE midterm;
```

Your project is currently configured in `src/main/resources/application.properties` with:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/midterm
spring.datasource.username=postgres
spring.datasource.password=Tracy@120
```

If your PostgreSQL port, username, or password is different, change those values before running the project.

## 2. Start The App
Run:
```bash
mvn spring-boot:run
```

Base URL:
```text
http://localhost:8080
```

Use header:
```text
Content-Type: application/json
```

## 3. Save Rwanda Locations In Order
You must save locations from top to bottom:
1. Province
2. District
3. Sector
4. Cell
5. Village

For every non-province request, use the returned parent `id` in the `parentId` query parameter.

### 3.1 Save Province
`POST /api/location/save`

```json
{
  "code": "KGL",
  "name": "Kigali City",
  "type": "PROVINCE"
}
```

Save the returned `id`. Example: `provinceId = 1`

### 3.2 Save District
`POST /api/location/save?parentId=1`

```json
{
  "code": "GAS",
  "name": "Gasabo",
  "type": "DISTRICT"
}
```

Save the returned `id`. Example: `districtId = 2`

### 3.3 Save Sector
`POST /api/location/save?parentId=2`

```json
{
  "code": "REM",
  "name": "Remera",
  "type": "SECTOR"
}
```

Save the returned `id`. Example: `sectorId = 3`

### 3.4 Save Cell
`POST /api/location/save?parentId=3`

```json
{
  "code": "RUK1",
  "name": "Rukiri I",
  "type": "CELL"
}
```

Save the returned `id`. Example: `cellId = 4`

### 3.5 Save Village
`POST /api/location/save?parentId=4`

```json
{
  "code": "VIL001",
  "name": "Village A",
  "type": "VILLAGE"
}
```

Save the returned `id`. Example: `villageId = 5`

### 3.5.1 Save Another Village Under The Same Cell
If you want to register a user with `Village B`, save it first.

`POST /api/location/save?parentId=4`

```json
{
  "code": "VIL002",
  "name": "Village B",
  "type": "VILLAGE"
}
```

### 3.6 Continue The Same Pattern
Follow the exact same pattern for the rest of the hierarchy from your trainer's instructions:
- Kigali City -> Gasabo -> Remera -> Rukiri I -> Village A/B/C
- Kigali City -> Gasabo -> Remera -> Rukiri II -> Village A/B
- Kigali City -> Gasabo -> Remera -> Nyabisindu -> Village A/B
- Kigali City -> Gasabo -> Kacyiru -> Kamatamu -> Village A/B
- Kigali City -> Gasabo -> Kagugu -> Village A/B
- Kigali City -> Kicukiro -> Kagarama -> Kanserege -> Village A/B
- Kigali City -> Kicukiro -> Kagarama -> Muyange -> Village A/B
- Kigali City -> Kicukiro -> Niboye -> Niboye -> Village A/B
- Kigali City -> Kicukiro -> Niboye -> Nyakabanda -> Village A/B
- Kigali City -> Nyarugenge -> Muhima -> Nyabugogo -> Village A/B
- Kigali City -> Nyarugenge -> Muhima -> Kabasengerezi -> Village A/B
- Kigali City -> Nyarugenge -> Nyamirambo -> Cyivugiza -> Village A/B
- Kigali City -> Nyarugenge -> Nyamirambo -> Rugarama -> Village A/B

Useful location endpoints:
- `GET /api/location/all`
- `GET /api/location/type/PROVINCE`
- `GET /api/location/type/DISTRICT`
- `GET /api/location/type/SECTOR`
- `GET /api/location/type/CELL`
- `GET /api/location/type/VILLAGE`
- `GET /api/location/{id}`
- `GET /api/location/province/Kigali City`

## 4. Create A User Using Only Village
Do not send province, district, sector, or cell in the user body.
Use only `villageCode` or `villageName`.

### 4.1 Register User With `villageCode`
`POST /api/user/register`

```json
{
  "userCode": "USR001",
  "firstName": "Alice",
  "lastName": "Mugisha",
  "email": "alice@example.com",
  "phoneNumber": "0788000000",
  "villageCode": "VIL001"
}
```

### 4.2 Register User With `villageName`
`POST /api/user/register`

```json
{
  "userCode": "USR002",
  "firstName": "Eric",
  "lastName": "Ndayisaba",
  "email": "eric@example.com",
  "phoneNumber": "0788222333",
  "villageName": "Village B"
}
```

This works only if `Village B` already exists in the database.

Use `villageCode` when village names repeat in different cells.

### 4.3 Read And Search Users
- `GET /api/user/{id}`
- `GET /api/users/all`
- `GET /api/user/search/name/Kigali City`
- `GET /api/user/search/code/KGL`
- `GET /api/user/search/location?provinceCode=KGL`
- `GET /api/user/search/location?districtCode=GAS`
- `GET /api/user/search/location?sectorCode=REM`
- `GET /api/user/search/location?cellCode=RUK1`
- `GET /api/user/search/location?villageCode=VIL001`

### 4.4 Update User
`PUT /api/user/update/{id}`

```json
{
  "userCode": "USR001",
  "firstName": "Alice",
  "lastName": "Uwase",
  "email": "alice@example.com",
  "phoneNumber": "0788000000",
  "villageCode": "VIL001"
}
```

### 4.5 Delete User
- `DELETE /api/user/delete/{id}`

## 5. Tour Guide Requests
### 5.1 Save Guide
`POST /api/guide/save`

```json
{
  "guideCode": "GD001",
  "fullName": "Jean Claude",
  "email": "guide1@example.com",
  "phoneNumber": "0788333444",
  "specialization": "Wildlife"
}
```

### 5.2 Read / Update / Delete Guide
- `GET /api/guides/all`
- `GET /api/guide/{id}`
- `PUT /api/guide/update/{id}`
- `DELETE /api/guide/delete/{id}`

## 6. Tour Package Requests
### 6.1 Save Package
If you already have guide IDs, attach them with `guideIds`.

`POST /api/package/save?guideIds=1&guideIds=2`

```json
{
  "packageCode": "PKG001",
  "title": "Akagera Weekend Tour",
  "description": "Two-day wildlife experience.",
  "price": 150000,
  "durationInDays": 2
}
```

If you have only one guide, use one `guideIds` value:

`POST /api/package/save?guideIds=1`

```json
{
  "packageCode": "PKG001",
  "title": "Akagera Weekend Tour",
  "description": "Two-day wildlife experience.",
  "price": 150000,
  "durationInDays": 2
}
```

Your current app already has guide `id = 1`, so this one-guide request should work.

### 6.2 Read / Update / Delete Package
- `GET /api/packages/all`
- `GET /api/package/{id}`
- `PUT /api/package/update/{id}`
- `DELETE /api/package/delete/{id}`

## 7. Booking Requests
### 7.1 Place Booking
Use the user ID and package ID in the path.

`POST /api/booking/place/1/1`

```json
{
  "bookingReference": "BK001",
  "travelDate": "2026-04-20",
  "numberOfPeople": 2,
  "status": "PENDING"
}
```

If you do not send `status`, the system saves `PENDING`.

### 7.2 Read / Update / Delete Booking
- `GET /api/booking/all`
- `GET /api/booking/{id}`
- `GET /api/booking/user/{userId}`
- `PUT /api/booking/update/{id}`
- `PATCH /api/booking/status/{id}?status=CONFIRMED`
- `DELETE /api/booking/delete/{id}`

## 8. Recommended Postman Order
1. Save all locations first.
2. Check villages with `GET /api/location/type/VILLAGE`.
3. Register users using `villageCode`.
4. Create guides.
5. Create tour packages.
6. Place bookings.
7. Search users by province or any location level.

## 8.1 Your Current Database State
Right now your running app already has:

- `KGL` -> `Kigali City`
- `GAS` -> `Gasabo`
- `REM` -> `Remera`
- `RUK1` -> `Rukiri I`
- `VIL001` -> `Village A`

So this request works now:

```json
{
  "userCode": "USR003",
  "firstName": "Eric",
  "lastName": "Ndayisaba",
  "email": "eric3@example.com",
  "phoneNumber": "0788222333",
  "villageCode": "VIL001"
}
```

If you want to use `Village B`, create it first with `VIL002`.

## 9. Important Rule From The Trainer
When creating a user, the request should point to the village only.
Because the village is already linked to the cell, sector, district, and province, the user can still be found by province, district, sector, cell, or village.

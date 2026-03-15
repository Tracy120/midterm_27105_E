# tour_booking

Simplified Spring Boot REST API for the midterm project.

## Structure
- Root package: `src/main/java/auca/ac/rw/tourbook`
- `controller`
- `model`
- `repository`
- `service`

## Main Rule
Save locations using the Rwanda hierarchy:
Province -> District -> Sector -> Cell -> Village

When creating a user, use only `villageCode` or `villageName`.

## Run
1. Make sure PostgreSQL is running.
2. Create the database `midterm`.
3. Check `src/main/resources/application.properties`.
4. Start the app with `mvn spring-boot:run`

## Testing
Use [POSTMAN_GUIDE.md](POSTMAN_GUIDE.md) for the full Postman flow.

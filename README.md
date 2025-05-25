# Hotel Backend Project

This project is a multi-module Spring Boot backend for a hotel management system, following an API-first development approach.

## Project Structure

- **booking-service/**: Handles booking-related operations.
- **crud-service/**: Handles CRUD operations for hotel entities.
- **common-dto/**: Shared DTOs and common code.

## API-First Approach

- API contracts are defined first using OpenAPI (Swagger) YAML files.
- See `crud-service/src/main/resources/api/crud-api.yaml` for the CRUD API definition.
- Backend implementation follows the contract defined in the YAML files.

## How to Run

### Prerequisites

- Java 21
- Gradle (or use the Gradle Wrapper if available)

### Build the Project

```sh
gradle build
```

### Run Services

In separate terminals, run:

```sh
gradle :booking-service:bootRun
```

```sh
gradle :crud-service:bootRun
```

### API Documentation

- The API contract for CRUD operations is available at: `crud-service/src/main/resources/api/crud-api.yaml`
- Use tools like Swagger UI or Redoc to visualize and interact with the API contract.

## API Certification & Validation

- The API contract is defined in `crud-service/src/main/resources/api/crud-api.yaml`.
- The running service automatically serves:
  - Swagger UI at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
  - OpenAPI JSON at: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- Requests and responses should conform to the OpenAPI contract for certification.
- Use contract-based tests or tools like Swagger Validator to ensure compliance.

## Docker Support

- Each service contains a `Dockerfile` for containerization.
- Use `docker-compose.yml` at the project root to run all services together.

## Contributing

- Update the OpenAPI YAML files first for any API changes.
- Implement backend logic to match the updated API contract.

## License

MIT

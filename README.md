# Hotel Management System

A comprehensive hotel management system built with Spring Boot and React.

## Services

The system consists of the following microservices:

1. **CRUD Service** (Port 8080)

   - Manages hotel, room, and amenity data
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - OpenAPI Spec: http://localhost:8080/v3/api-docs

2. **User Service** (Port 8081)

   - Handles user management and authentication
   - Swagger UI: http://localhost:8081/swagger-ui.html
   - OpenAPI Spec: http://localhost:8081/v3/api-docs

3. **Booking Service** (Port 8082)

   - Manages room bookings and availability
   - Swagger UI: http://localhost:8082/swagger-ui.html
   - OpenAPI Spec: http://localhost:8082/v3/api-docs

4. **Payment Service** (Port 8083)
   - Handles payment processing and transactions
   - Swagger UI: http://localhost:8083/swagger-ui.html
   - OpenAPI Spec: http://localhost:8083/v3/api-docs

## API Documentation

Each service provides comprehensive API documentation using OpenAPI 3.0 (Swagger). The documentation includes:

- Detailed endpoint descriptions
- Request/response schemas
- Authentication requirements
- Example requests and responses
- Error codes and handling

### Authentication

All APIs require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Common Response Formats

#### Success Response

```json
{
  "data": {
    // Response data
  },
  "message": "Operation successful",
  "timestamp": "2024-03-14T12:00:00Z"
}
```

#### Error Response

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Error description",
    "details": {
      // Additional error details
    }
  },
  "timestamp": "2024-03-14T12:00:00Z"
}
```

## Development

### Prerequisites

- Java 21
- Node.js 18+
- PostgreSQL 15+
- Docker (optional)

### Running Locally

1. Clone the repository:

```bash
git clone https://github.com/yourusername/hotel-management.git
cd hotel-management
```

2. Start the services:

```bash
./gradlew :crud-service:bootRun
./gradlew :user-service:bootRun
./gradlew :booking-service:bootRun
./gradlew :payment-service:bootRun
```

3. Access the Swagger UI for each service using the URLs provided above.

### Running with Docker

1. Build the Docker images:

```bash
docker-compose build
```

2. Start the services:

```bash
docker-compose up
```

## Testing

Run the test suite:

```bash
./gradlew test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Production Security Considerations

### HTTPS Configuration

- The application is configured to use HTTPS in production
- SSL/TLS certificates are managed through environment variables:
  - `KEYSTORE_PASSWORD`: Password for the keystore
  - `KEY_PASSWORD`: Password for the private key
- Keystore file (`keystore.p12`) should be placed in the classpath
- SSL configuration is applied to both main application and management endpoints

### CORS Configuration

- CORS is configured to allow specific origins only
- Default configuration:
  - Allowed Origins: `https://hotel-frontend.example.com`, `https://admin.hotel.example.com`
  - Allowed Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
  - Allowed Headers: Authorization, Content-Type, X-Requested-With, X-API-Key
  - Max Age: 3600 seconds
- Configuration can be customized through environment variables

### API Key Management

- API keys are used for machine-to-machine communication
- Features:
  - Automatic key rotation (configurable interval)
  - Grace period for key transitions
  - Maximum number of active keys
  - Secure key generation
- Configuration:
  ```yaml
  app:
    api:
      key:
        rotation:
          enabled: true
          interval: 30d
          grace-period: 7d
          max-keys: 3
  ```

### Environment Variables

Required environment variables for production:

```bash
# SSL Configuration
KEYSTORE_PASSWORD=your-keystore-password
KEY_PASSWORD=your-key-password

# Database Configuration
DB_USERNAME=your-db-username
DB_PASSWORD=your-db-password

# JWT Configuration
JWT_SECRET=your-jwt-secret
AUTH_SERVER_URL=https://auth.example.com

# CORS Configuration (optional)
APP_CORS_ALLOWED_ORIGINS=https://frontend1.example.com,https://frontend2.example.com
```

### Security Best Practices

1. **SSL/TLS**

   - Use strong TLS 1.3
   - Regular certificate rotation
   - HSTS enabled
   - Secure cipher suites

2. **API Security**

   - Rate limiting enabled
   - Request validation
   - Input sanitization
   - XSS protection

3. **Authentication**

   - JWT with short expiration
   - Secure token storage
   - Role-based access control
   - API key rotation

4. **Monitoring**
   - Security event logging
   - Audit trails
   - Health checks
   - Metrics collection

### Deployment Checklist

- [ ] SSL certificates installed and configured
- [ ] Environment variables set
- [ ] CORS origins configured
- [ ] API keys generated and distributed
- [ ] Database credentials secured
- [ ] JWT secrets rotated
- [ ] Security headers configured
- [ ] Monitoring enabled
- [ ] Backup strategy implemented
- [ ] Disaster recovery plan in place

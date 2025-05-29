# User Service Test Configuration

This directory contains test configuration files and resources for the User Service.

## Test Configuration Overview

The test configuration uses the following components:

### Database Configuration

- Uses H2 in-memory database for testing
- Database URL: `r2dbc:h2:mem://testdb;DB_CLOSE_DELAY=-1`
- Username: `sa`
- No password required
- Schema is automatically created on startup

### Security Configuration

- JWT Secret Key: `testSecretKey1234567890123456789012345678901234567890`
- Access Token Expiration: 1 hour
- Refresh Token Expiration: 7 days

### Resilience4j Configuration

#### Rate Limiter

```yaml
rate-limiter:
  user-registration:
    limitForPeriod: 5
    limitRefreshPeriod: 1m
  user-login:
    limitForPeriod: 10
    limitRefreshPeriod: 1m
  default:
    limitForPeriod: 100
    limitRefreshPeriod: 1m
```

#### Circuit Breaker

```yaml
circuit-breaker:
  instances:
    user-service:
      failureRateThreshold: 50
      minimumNumberOfCalls: 5
      automaticTransitionFromOpenToHalfOpenEnabled: true
      waitDurationInOpenState: 60s
      permittedNumberOfCallsInHalfOpenState: 3
```

#### Retry

```yaml
retry:
  instances:
    user-service:
      maxAttempts: 3
      waitDuration: 1s
      enableExponentialBackoff: true
      exponentialBackoffMultiplier: 2
```

#### Bulkhead

```yaml
bulkhead:
  instances:
    user-service:
      maxConcurrentCalls: 20
      maxWaitDuration: 500ms
```

### Management Endpoints

- Health: `/actuator/health`
- Info: `/actuator/info`
- Metrics: `/actuator/metrics`
- Prometheus: `/actuator/prometheus`

### Logging Configuration

- Debug level for application packages
- Info level for other packages

## Running Tests

1. Unit Tests:

```bash
./gradlew :user-service:test
```

2. Integration Tests:

```bash
./gradlew :user-service:integrationTest
```

3. All Tests:

```bash
./gradlew :user-service:check
```

## Test Data

Test data is automatically loaded from `data.sql` in the test resources directory. This includes:

- Sample users with different roles
- Test credentials for authentication
- Sample tokens for testing

## Test Profiles

The application uses the following test profiles:

- `test`: Default test profile
- `test-no-security`: Profile for testing without security
- `test-with-mock`: Profile for testing with mocked dependencies

To run tests with a specific profile:

```bash
./gradlew :user-service:test --args='--spring.profiles.active=test-with-mock'
```

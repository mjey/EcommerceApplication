# API Gateway Service

## Overview
The API Gateway serves as the single entry point for all client requests in the E-commerce microservices architecture. It handles routing, authentication, rate limiting, and circuit breaking.

## Design Patterns Implemented

### 1. **Gateway Pattern**
- Single entry point for all microservices
- Centralizes cross-cutting concerns

### 2. **Circuit Breaker Pattern**
- Prevents cascading failures using Resilience4j
- Provides fallback responses when services are down

### 3. **Filter Pattern**
- `AuthenticationFilter`: Validates JWT tokens
- `LoggingFilter`: Logs all requests and responses

### 4. **Service Discovery**
- Integrates with Eureka for dynamic service routing
- Load balancing via `lb://` URI scheme

### 5. **Rate Limiting**
- Redis-based rate limiting to prevent abuse
- Configurable per route

## Architecture

```
Client Request
    ↓
API Gateway (Port 8080)
    ├── LoggingFilter (logs all requests)
    ├── AuthenticationFilter (validates JWT for protected routes)
    ├── Rate Limiter (Redis-based)
    ├── Circuit Breaker (Resilience4j)
    └── Routes to Services via Eureka
        ├── Auth Service (lb://auth-service)
        ├── Users Service (lb://users-service)
        ├── Orders Service (lb://orders-service)
        └── Payments Service (lb://payments-service)
```

### Public Routes (No Authentication)
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh token

### Protected Routes (Requires JWT)
- `GET /api/v1/users/**` - User management
- `GET /api/v1/orders/**` - Order management
- `GET /api/v1/payments/**` - Payment processing

## Configuration

## Running with Docker

### Build and Run
```bash
docker-compose up -d
```

### Check Logs
```bash
docker logs api-gateway -f
```

### Access Gateway
```
http://localhost:8080
```

## Rate Limiting

Default rate limits:
- Auth Service: 10 requests/second (burst: 20)
- Users Service: 20 requests/second (burst: 40)
- Orders Service: 15 requests/second (burst: 30)
- Payments Service: 10 requests/second (burst: 20)

## Monitoring

### Health Check
```
GET http://localhost:8080/actuator/health
```

### Circuit Breaker Status
```
GET http://localhost:8080/actuator/circuitbreakers
```

### Gateway Routes
```
GET http://localhost:8080/actuator/gateway/routes
```

## Request Flow Example

### Protected Request with JWT
```bash
curl -X GET http://localhost:8080/api/v1/users/profile \
  -H "Authorization: Bearer <jwt_token>"
```

### Headers Added to Downstream Requests
- `X-User-Username`: Authenticated user's username
- `X-User-Roles`: User's roles
- `X-Gateway`: Gateway identifier

## Error Handling

### Circuit Breaker Open
```json
{
  "timestamp": "2025-11-20T13:30:00",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Users Service is temporarily unavailable",
  "circuitBreakerActivated": true
}
```

### Rate Limit Exceeded
```json
{
  "status": 429,
  "error": "Too Many Requests"
}
```

### Invalid JWT
```json
{
  "timestamp": "2025-11-20T13:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

## Dependencies

- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Resilience4j (Circuit Breaker)
- Spring Data Redis (Rate Limiting)
- JJWT (JWT Processing)

## Integration with Other Services

### Auth Service
- Validates JWT tokens using shared secret
- No API calls needed for validation (stateless)

### Eureka Server
- Registers as a client
- Fetches service registry
- Uses for dynamic routing

### User/Order/Payment Services
- Routes requests based on path
- Adds authentication headers
- Handles circuit breaking and retries

## Development

### Local Testing Without Docker
```yaml
# application.yaml for local development
spring:
  data:
    redis:
      host: localhost
      port: 6379

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Testing Circuit Breaker
Stop a service to trigger circuit breaker:
```bash
docker stop user-service
curl http://localhost:8080/api/v1/users/profile
# Returns fallback response
```

### Testing Rate Limiting
```bash
# Send rapid requests
for i in {1..30}; do 
  curl http://localhost:8080/api/v1/auth/login
done
# Some requests will be rate limited
```

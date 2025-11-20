# Auth Service - Docker Setup

Ecommerce Auth Service.

## Prerequisites

- Docker 20.10+
- Docker Compose 2.0+

## Quick Start

```bash
# Start all services (PostgreSQL, Kafka, Auth Service)
docker-compose up -d

# View logs
docker-compose logs -f auth-service

# Stop all services
docker-compose down
```

## Service Access

- **Auth Service**: http://localhost:8081
- **Health Check**: http://localhost:8081/actuator/health
- **PostgreSQL**: localhost:5432
- **Kafka**: localhost:9092


## API Examples

```bash
# 1. Register a new user
curl -X POST http://localhost:8081/api/v1/auth/register \
   -H "Content-Type: application/json" \
   -d '{
      "username": "john_doe",
      "email": "john@example.com",
      "password": "password123",
      "firstName": "John",
      "lastName": "Doe"
   }'

# 2. Login
curl -X POST http://localhost:8081/api/v1/auth/login \
   -H "Content-Type: application/json" \
   -d '{
      "usernameOrEmail": "john_doe",
      "password": "password123"
   }'

# 3. Validate token (use token from login response)
curl -X POST http://localhost:8081/api/v1/auth/validate \
   -H "Content-Type: application/json" \
   -d '{
      "token": "YOUR_JWT_TOKEN_HERE"
   }'
```

## Building the Image

### Build locally
```bash
docker build -t auth-service:latest .
```

### Build with specific tag
```bash
docker build -t your-registry/auth-service:1.0.0 .
```

### Push to registry
```bash
docker tag auth-service:latest your-registry/auth-service:1.0.0
docker push your-registry/auth-service:1.0.0
```

## Troubleshooting

### Check service health
```bash
docker-compose ps
docker-compose logs auth-service
```

### Check database connection
```bash
docker-compose exec postgres psql -U postgres -d auth_db -c "SELECT 1"
```

## Support

For issues or questions, please check the logs:
```bash
docker-compose logs -f auth-service
```

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

# Eureka Service Discovery
logs
## Build & Start
From the directory containing the docker-compose.yml:
```
docker-compose up -d --build
```

## View logs
```
docker-compose logs -f
```
(or `docker-compose logs -f <service-name>` to follow a specific service)

## Stop
```
docker-compose down
```

## Access
Once running, open the Eureka dashboard:
http://localhost:8761
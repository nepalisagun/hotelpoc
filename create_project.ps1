# Script to create the Hotel Backend Scaffold project structure

Write-Host "Creating root project structure..."

# Create root files
New-Item -ItemType File -Name "build.gradle" -Force | Out-Null
New-Item -ItemType File -Name "settings.gradle" -Force | Out-Null
New-Item -ItemType File -Name "docker-compose.yml" -Force | Out-Null

# Create 'common-dto' structure
Write-Host "Creating common-dto module..."
New-Item -ItemType Directory -Path "common-dto/src/main/java/com/example/hotel/common/dto" -Force | Out-Null
New-Item -ItemType File -Path "common-dto/build.gradle" -Force | Out-Null
New-Item -ItemType File -Path "common-dto/src/main/java/com/example/hotel/common/dto/BookingCreatedEvent.java" -Force | Out-Null
New-Item -ItemType File -Path "common-dto/src/main/java/com/example/hotel/common/dto/HotelDto.java" -Force | Out-Null

# Create 'crud-service' structure
Write-Host "Creating crud-service module..."
New-Item -ItemType Directory -Path "crud-service/src/main/java/com/example/hotel/crud/config" -Force | Out-Null
New-Item -ItemType Directory -Path "crud-service/src/main/java/com/example/hotel/crud/model" -Force | Out-Null
New-Item -ItemType Directory -Path "crud-service/src/main/java/com/example/hotel/crud/repository" -Force | Out-Null
New-Item -ItemType Directory -Path "crud-service/src/main/java/com/example/hotel/crud/controller" -Force | Out-Null
New-Item -ItemType Directory -Path "crud-service/src/main/resources/api" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/build.gradle" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/Dockerfile" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/src/main/java/com/example/hotel/crud/CrudServiceApplication.java" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/src/main/java/com/example/hotel/crud/config/R2dbcConfig.java" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/src/main/java/com/example/hotel/crud/model/Hotel.java" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/src/main/java/com/example/hotel/crud/repository/HotelRepository.java" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/src/main/java/com/example/hotel/crud/controller/HotelControllerImpl.java" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/src/main/resources/api/crud-api.yaml" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/src/main/resources/application.yml" -Force | Out-Null
New-Item -ItemType File -Path "crud-service/src/main/resources/schema.sql" -Force | Out-Null

# Create 'booking-service' structure
Write-Host "Creating booking-service module..."
New-Item -ItemType Directory -Path "booking-service/src/main/java/com/example/hotel/booking/client" -Force | Out-Null
New-Item -ItemType Directory -Path "booking-service/src/main/java/com/example/hotel/booking/config" -Force | Out-Null
New-Item -ItemType Directory -Path "booking-service/src/main/java/com/example/hotel/booking/controller" -Force | Out-Null
New-Item -ItemType Directory -Path "booking-service/src/main/java/com/example/hotel/booking/kafka" -Force | Out-Null
New-Item -ItemType Directory -Path "booking-service/src/main/java/com/example/hotel/booking/service" -Force | Out-Null
New-Item -ItemType Directory -Path "booking-service/src/main/resources" -Force | Out-Null
New-Item -ItemType File -Path "booking-service/build.gradle" -Force | Out-Null
New-Item -ItemType File -Path "booking-service/Dockerfile" -Force | Out-Null
New-Item -ItemType File -Path "booking-service/src/main/java/com/example/hotel/booking/BookingServiceApplication.java" -Force | Out-Null
New-Item -ItemType File -Path "booking-service/src/main/java/com/example/hotel/booking/client/CrudApiClient.java" -Force | Out-Null
New-Item -ItemType File -Path "booking-service/src/main/java/com/example/hotel/booking/config/WebClientConfig.java" -Force | Out-Null
New-Item -ItemType File -Path "booking-service/src/main/java/com/example/hotel/booking/controller/BookingController.java" -Force | Out-Null
New-Item -ItemType File -Path "booking-service/src/main/java/com/example/hotel/booking/kafka/BookingEventProducer.java" -Force | Out-Null
New-Item -ItemType File -Path "booking-service/src/main/java/com/example/hotel/booking/service/BookingService.java" -Force | Out-Null
New-Item -ItemType File -Path "booking-service/src/main/resources/application.yml" -Force | Out-Null

Write-Host "Adding initial content to Gradle files..."

# settings.gradle
@"
rootProject.name = 'hotel-backend-scaffold'

include 'common-dto'
include 'crud-service'
include 'booking-service'
"@ | Set-Content -Path "settings.gradle"

# build.gradle (Root) - Using a multiline string
@"
plugins {
    id 'org.springframework.boot' version '3.2.5' apply false
    id 'io.spring.dependency-management' version '1.1.4' apply false
    id 'java'
}

subprojects {
    apply plugin: 'java'
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'

    group = 'com.example.hotel'
    version = '0.0.1-SNAPSHOT'

    java {
        sourceCompatibility = '21'
    }

    configurations {
        compileOnly {
            extendsFrom annotationProcessor
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly 'org.projectlombok:lombok'
        annotationProcessor 'org.projectlombok:lombok'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
    }

    tasks.named('test') {
        useJUnitPlatform()
    }
}
"@ | Set-Content -Path "build.gradle"

# common-dto/build.gradle
@"
plugins {
    id 'java-library'
}

dependencies {
    api 'jakarta.validation:jakarta.validation-api:3.0.2'
    api 'com.fasterxml.jackson.core:jackson-annotations:2.15.4'

    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}
"@ | Set-Content -Path "common-dto/build.gradle"

# Minimal service build.gradle files
"apply plugin: 'org.springframework.boot'`napply plugin: 'java'`ndependencies { implementation project(':common-dto') }" | Set-Content -Path "crud-service/build.gradle"
"apply plugin: 'org.springframework.boot'`napply plugin: 'java'`ndependencies { implementation project(':common-dto') }" | Set-Content -Path "booking-service/build.gradle"


Write-Host "Adding placeholder Dockerfiles and docker-compose.yml..."
"# Placeholder Dockerfile for crud-service" | Set-Content -Path "crud-service/Dockerfile"
"# Placeholder Dockerfile for booking-service" | Set-Content -Path "booking-service/Dockerfile"

# docker-compose.yml
@"
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.3
    container_name: zookeeper
    networks: [spring-net]
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.3.3
    container_name: kafka
    depends_on: [zookeeper]
    networks: [spring-net]
    ports: ["9092:9092", "29092:29092"]
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  crud-service:
    build: ./crud-service
    container_name: crud-service
    networks: [spring-net]
    ports: ["8080:8080"]
    depends_on: [kafka]

  booking-service:
    build: ./booking-service
    container_name: booking-service
    networks: [spring-net]
    ports: ["8081:8081"]
    depends_on: [kafka, crud-service]
    environment:
      CRUD_SERVICE_URL: http://crud-service:8080
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092

networks:
  spring-net:
    driver: bridge
"@ | Set-Content -Path "docker-compose.yml"

Write-Host "Project structure created successfully!"
Write-Host "Next steps:"
Write-Host "1. Review the generated files, especially build.gradle for each module."
Write-Host "2. Copy the detailed code from our previous discussion into the .java files."
Write-Host "3. Refine Dockerfiles if necessary."
Write-Host "4. Run 'gradlew.bat build' (or ./gradlew build) to ensure everything compiles."
# StorageMan Backend

Spring Boot backend for the StorageMan cloud storage management application.

## Tech Stack

- **Java 21** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data access
- **PostgreSQL** - Production database
- **H2** - Test database
- **JWT** - Authentication
- **SpringDoc OpenAPI** - API documentation
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **REST Assured** - API integration testing

## Prerequisites

- Java 21 (JDK)
- Maven 3.6+
- PostgreSQL (for production)

## Setup

1. Configure PostgreSQL database (create a database named `storageman`)

2. Create `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/storageman
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=your-secret-key-at-least-256-bits-long
jwt.expiration=86400000

# Server
server.port=8080
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

## Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Classes

```bash
# Run controller tests
mvn test -Dtest=AuthControllerTest

# Run service tests
mvn test -Dtest=JwtServiceTest

# Run integration tests
mvn test -Dtest=StorageItemIntegrationTest
```

### Test Coverage

Generate test coverage report:
```bash
mvn clean verify
```

### Test Structure

```
src/test/java/
├── com/anode/storage/
│   ├── controller/          # Controller layer tests
│   │   └── AuthControllerTest.java
│   ├── service/             # Service layer tests
│   │   └── JwtServiceTest.java
│   ├── repository/          # Repository layer tests
│   │   └── RepositoryTest.java
│   └── integration/         # Integration tests
│       └── StorageItemIntegrationTest.java
```

### Test Configuration

Tests use an in-memory H2 database configured in `src/test/resources/application-test.properties`.

Key features:
- **H2 In-Memory Database** - Fast, isolated test data
- **Auto DDL** - Schema created/dropped per test
- **Mock Authentication** - Test JWT secret
- **Disabled OpenAPI** - Reduces test overhead

## API Documentation

When running, API documentation is available at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Project Structure

```
back/
├── src/main/java/com/anode/storage/
│   ├── config/              # Configuration classes
│   │   ├── OpenApiConfig.java
│   │   └── WebSocketConfig.java
│   ├── controller/          # REST controllers
│   ├── dto/                 # Data Transfer Objects
│   ├── entity/              # JPA entities
│   ├── repository/          # JPA repositories
│   ├── security/            # Security & JWT
│   ├── service/             # Business logic
│   └── StorageManApp.java   # Main application class
├── src/test/                # Test sources
└── pom.xml                  # Maven configuration
```

## Development

### Build WAR File

```bash
mvn clean package
```

The WAR file will be created in `target/storage-0.0.1-SNAPSHOT.war`

### Run with Profile

```bash
# Development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Debug Mode

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## Testing Best Practices

### Unit Tests
- Test individual components in isolation
- Use `@MockBean` for dependencies
- Fast execution (< 1 second per test)

### Integration Tests
- Test multiple components together
- Use `@SpringBootTest` with random port
- Test actual HTTP endpoints with REST Assured

### Repository Tests
- Use `@DataJpaTest` for lightweight tests
- Test custom query methods
- Verify entity relationships

### Controller Tests
- Use `@WebMvcTest` for focused tests
- Mock service layer
- Test request/response mapping

## Common Maven Commands

```bash
mvn clean                    # Clean build artifacts
mvn compile                  # Compile source code
mvn test                     # Run tests
mvn package                  # Create WAR file
mvn spring-boot:run          # Run application
mvn dependency:tree          # Show dependency tree
mvn versions:display-updates # Check for updates
```

## Troubleshooting

### Tests Failing
1. Ensure H2 database dependency is present
2. Check `application-test.properties` configuration
3. Verify test profile is active

### Database Connection Issues
1. Verify PostgreSQL is running
2. Check database credentials
3. Ensure database exists

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

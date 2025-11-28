# Testing Guide

Complete guide for testing the StorageMan application.

## Overview

StorageMan uses a comprehensive testing strategy:

- **Frontend**: End-to-End (E2E) testing with Playwright
- **Backend**: Unit and Integration testing with JUnit 5

## Frontend Testing (Playwright)

### Setup

```bash
cd front
pnpm install
npx playwright install
```

### Running Tests

```bash
# Headless mode (CI/CD)
pnpm test:e2e

# UI mode - Interactive test runner (recommended for development)
pnpm test:e2e:ui

# Headed mode - See the browser
pnpm test:e2e:headed

# Debug mode - Step through tests
pnpm test:e2e:debug

# View last test report
pnpm test:e2e:report
```

### Test Files

Located in `front/e2e/`:

- `app.spec.ts` - Basic application functionality
  - Title rendering
  - Description display
  - Counter interaction
  - Dark theme styling

- `example-api.spec.ts` - API integration tests
  - Mock API responses
  - Error handling
  - Authentication headers

### Writing Tests

Example test structure:

```typescript
import { test, expect } from '@playwright/test';

test.describe('Feature Name', () => {
  test('should do something', async ({ page }) => {
    await page.goto('/');

    const element = page.getByRole('button', { name: /Click me/i });
    await expect(element).toBeVisible();

    await element.click();
    await expect(page.getByText('Success')).toBeVisible();
  });
});
```

### Configuration

`playwright.config.ts` settings:

- **Base URL**: http://localhost:5173
- **Browsers**: Chromium, Firefox, WebKit
- **Mobile**: Pixel 5, iPhone 12
- **Web Server**: Auto-starts Vite dev server
- **Reporter**: HTML report
- **Screenshots**: On failure only
- **Trace**: On first retry

### CI/CD Configuration

For GitHub Actions, add:

```yaml
- name: Install Playwright Browsers
  run: npx playwright install --with-deps

- name: Run E2E Tests
  run: pnpm test:e2e

- name: Upload Playwright Report
  uses: actions/upload-artifact@v4
  if: always()
  with:
    name: playwright-report
    path: playwright-report/
```

## Backend Testing (JUnit 5)

### Setup

```bash
cd back
mvn clean install
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Run specific test method
mvn test -Dtest=AuthControllerTest#testLoginWithValidFormat

# Run tests with coverage
mvn clean verify

# Skip tests during build
mvn package -DskipTests
```

### Test Structure

Located in `back/src/test/java/com/anode/storage/`:

#### 1. Controller Tests
`controller/AuthControllerTest.java`
- HTTP request/response testing
- JSON validation
- Status code verification

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLoginWithValidFormat() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
}
```

#### 2. Service Tests
`service/JwtServiceTest.java`
- Business logic testing
- Token generation/validation
- Exception handling

```java
@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken("user@example.com");
        assertNotNull(token);
    }
}
```

#### 3. Repository Tests
`repository/RepositoryTest.java`
- Database operations
- Custom queries
- Entity relationships

```java
@DataJpaTest
@ActiveProfiles("test")
class RepositoryTest {
    @Autowired
    private StorageItemRepository repository;

    @Test
    void testSaveAndRetrieve() {
        StorageItem item = repository.save(new StorageItem());
        assertNotNull(item.getId());
    }
}
```

#### 4. Integration Tests
`integration/StorageItemIntegrationTest.java`
- Full HTTP request cycle
- REST Assured for API testing
- Random port testing

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class StorageItemIntegrationTest {
    @LocalServerPort
    private int port;

    @Test
    void testEndpoint() {
        given()
            .port(port)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/items")
        .then()
            .statusCode(200);
    }
}
```

### Test Configuration

`src/test/resources/application-test.properties`:

- **Database**: H2 in-memory
- **DDL**: Auto create-drop
- **SQL Logging**: Enabled
- **JWT**: Test secret key
- **OpenAPI**: Disabled for tests

### Test Annotations

Common annotations used:

- `@SpringBootTest` - Full application context
- `@WebMvcTest` - Controller layer only
- `@DataJpaTest` - Repository layer only
- `@AutoConfigureMockMvc` - MockMvc support
- `@ActiveProfiles("test")` - Load test profile
- `@MockBean` - Mock dependencies
- `@DisplayName` - Readable test names

## Best Practices

### Frontend E2E

✅ **Do:**
- Use semantic queries (`getByRole`, `getByLabel`)
- Test user workflows, not implementation
- Use Page Object Model for complex pages
- Mock external APIs in tests
- Run tests in parallel when possible

❌ **Don't:**
- Use CSS selectors unless necessary
- Test implementation details
- Make tests dependent on each other
- Use fixed waits (use `waitFor` instead)
- Test third-party libraries

### Backend Unit/Integration

✅ **Do:**
- Follow AAA pattern (Arrange, Act, Assert)
- Use descriptive test names
- Test edge cases and error handling
- Use `@DisplayName` for clarity
- Clean up test data
- Use test profiles

❌ **Don't:**
- Test Spring Boot's functionality
- Write slow tests
- Use production database
- Share state between tests
- Skip assertions

## Coverage Goals

- **Frontend E2E**: Critical user paths (80%+)
- **Backend Unit**: Business logic (80%+)
- **Backend Integration**: API endpoints (70%+)

## Debugging

### Frontend (Playwright)

```bash
# Visual debugger
pnpm test:e2e:debug

# Trace viewer (after test run)
npx playwright show-trace trace.zip
```

### Backend (Java)

```bash
# Run tests with debugger
mvn test -Dmaven.surefire.debug

# Connect debugger to port 5005
```

## Continuous Integration

### Frontend CI

```yaml
name: E2E Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v2
      - run: pnpm install
      - run: npx playwright install --with-deps
      - run: pnpm test:e2e
```

### Backend CI

```yaml
name: Backend Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
      - run: mvn clean verify
```

## Troubleshooting

### Playwright Issues

**Browser not installed:**
```bash
npx playwright install chromium
```

**Port 5173 in use:**
```bash
# Config will auto-find available port
# Or kill existing process
lsof -ti:5173 | xargs kill
```

### Maven Test Issues

**Tests not found:**
```bash
mvn clean test
```

**H2 database issues:**
- Check `application-test.properties` exists
- Verify `@ActiveProfiles("test")` on test class

**Port 8080 in use:**
```bash
# Use random port in integration tests
@SpringBootTest(webEnvironment = RANDOM_PORT)
```

## Resources

- [Playwright Documentation](https://playwright.dev/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [REST Assured Guide](https://rest-assured.io/)

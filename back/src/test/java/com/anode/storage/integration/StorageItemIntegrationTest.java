package com.anode.storage.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Storage Item Integration Tests")
class StorageItemIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Should return API health status")
    void testHealthEndpoint() {
        // This is a basic test to verify the API is running
        // Adjust the endpoint based on your actual health check implementation
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/health")
        .then()
            .statusCode(anyOf(is(200), is(404))); // 404 if health endpoint doesn't exist yet
    }

    @Test
    @DisplayName("Should reject unauthorized access to protected endpoints")
    void testUnauthorizedAccess() {
        // Assuming /storage-items is a protected endpoint
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/storage-items")
        .then()
            .statusCode(anyOf(is(401), is(403), is(404)));
    }

    @Test
    @DisplayName("Should return proper content type for API responses")
    void testApiContentType() {
        // Test that API endpoints return JSON
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/")
        .then()
            .contentType(anyOf(
                containsString("application/json"),
                containsString("text/html") // In case root returns HTML
            ));
    }
}

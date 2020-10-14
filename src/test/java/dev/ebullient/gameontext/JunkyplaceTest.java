package dev.ebullient.gameontext;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JunkyplaceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/junkyplace/hello")
                .then()
                .statusCode(200)
                .body(is("hello"));
    }

    @Test
    public void testPrometheusEndpoint() {
        given()
                .when().get("/metrics")
                .then()
                .statusCode(200);
    }

    @Test
    public void testLivenessEndpoint() {
        given()
                .when().get("/health/live")
                .then()
                .statusCode(200);
    }

    @Test
    public void testReadinessEndpoint() {
        given()
                .when().get("/health/ready")
                .then()
                .statusCode(200);
    }
}

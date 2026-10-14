package it.zucchetti.lab;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Test per tutti gli endpoint di GreetingResource.
 * Usa il profilo "test" di Quarkus, dove app.crash.exit=false
 * per evitare che /crash spenga la JVM durante i test.
 */
@QuarkusTest
class GreetingResourceTest {

    // ---------- GET / ----------

    @Test
    void testIndexReturnsMessageAndHostname() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                // Il body deve contenere il messaggio di default e la parola "Hostname"
                .body(containsString("Hello from Quarkus!"))
                .body(containsString("Hostname:"))
                .body(containsString("Version:"));
    }

    // ---------- GET /info ----------

    @Test
    void testInfoReturnsJson() {
        given()
            .when().get("/info")
            .then()
                .statusCode(200)
                .contentType("application/json")
                // Il JSON deve avere le tre chiavi previste
                .body("hostname", notNullValue())
                .body("version", notNullValue())
                .body("startedAt", notNullValue());
    }

    // ---------- GET /health ----------

    @Test
    void testHealthReturnsOk() {
        given()
            .when().get("/health")
            .then()
                .statusCode(200)
                .contentType("application/json")
                .body("status", equalTo("ok"));
    }

    // ---------- POST /crash ----------

    @Test
    void testCrashDoesNotExitDuringTests() {
        // Nel profilo test, app.crash.exit=false, quindi la JVM NON si spegne.
        given()
            .when().post("/crash")
            .then()
                .statusCode(200)
                .contentType("application/json")
                .body("message", equalTo("Shutting down..."));
    }
}


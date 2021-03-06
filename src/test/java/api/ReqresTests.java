package api;

import api.model.LombokUserData;
import api.model.RegisterData;
import api.model.UserWithJob;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static api.filters.CustomLogFilter.customLogFilter;
import static api.specification.ReqresSpecs.request;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReqresTests {

    private final UserWithJob userWithJob = UserWithJob.builder()
            .name("morpheus")
            .job("zion resident")
            .build();

    @Test
    @Feature("REST API")
    @DisplayName("Single user")
    void singleUser() {
        LombokUserData data = given()
                .filter(customLogFilter().withCustomTemplates())
                .baseUri("https://reqres.in")
                .basePath("/api")
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .log().body()
                .extract().as(LombokUserData.class);

        assertEquals("Janet", data.getUser().getFirstName());
    }

    @Test
    @Feature("REST API")
    @DisplayName("Register successful")
    void registerSuccessful() {
        RegisterData registerData = new RegisterData();
        registerData.setEmail("eve.holt@reqres.in");
        registerData.setPassword("pistol");
        given()
                .filter(customLogFilter().withCustomTemplates())
                .contentType(JSON)
                .body(registerData)
                .when()
                .post(("https://reqres.in/api/register"))
                .then()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @Feature("REST API")
    @DisplayName("Register unsuccessful")
    void registerUnsuccessful() {
        RegisterData registerData = new RegisterData();
        registerData.setEmail("sydney@fife");
        given()
                .filter(customLogFilter().withCustomTemplates())
                .contentType(JSON)
                .body(registerData)
                .when()
                .post(("https://reqres.in/api/register"))
                .then()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    @Feature("REST API")
    @DisplayName("Update put")
    void updatePut() {
        given()
                .spec(request)
                .body(userWithJob)
                .when()
                .put(("https://reqres.in/api/users?page=2"))
                .then()
                .statusCode(200)
                .body("name", is(userWithJob.getName()));
    }

    @Test
    @Feature("REST API")
    @DisplayName("Update patch")
    void updatePatch() {
        given()
                .spec(request)
                .body(userWithJob)
                .when()
                .patch(("https://reqres.in/api/users?page=2"))
                .then()
                .statusCode(200)
                .body("job", is(userWithJob.getJob()));
    }

    @Test
    @Feature("REST API")
    @DisplayName("Check resource name")
    public void checkResourceNameGroovy() {
        given()
                .spec(request)
                .when()
                .get(("https://reqres.in/api/unknown"))
                .then()
                .log().body()
                .body("data.findAll{it.name}.name.flatten()",
                        hasItem("true red"));
    }
}
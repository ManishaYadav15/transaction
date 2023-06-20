package org.acme.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import org.acme.dto.AuthRequest;
import org.acme.entity.User;
import org.acme.service.UserService;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class AuthenticationTest {

    @InjectMock
    UserService userService;

    final String SIGN_UP_ENDPOINT = "/user/signup";
    final String LOG_IN_ENDPOINT = "/user/login";
    final String username = "john";
    final String password = "passworD.1234";
    final AuthRequest authRequest = new AuthRequest(username, password);

    @Test
    void test_login() {
        User user = new User(username, password);

        when(userService.findUser(any())).thenReturn(user);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authRequest)
                .when()
                .post(LOG_IN_ENDPOINT)
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    void test_login_unauthorized_user() {
        when(userService.findUser(any())).thenReturn(null);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authRequest)
                .when()
                .post(LOG_IN_ENDPOINT)
                .then()
                .statusCode(401);
    }

    @Test
    void test_login_incorrect_password() {
        User user = new User(username, "Wrong.1password");

        when(userService.findUser(any())).thenReturn(user);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authRequest)
                .when()
                .post(LOG_IN_ENDPOINT)
                .then()
                .statusCode(401);
    }

    @Test
    void test_login_user_not_found() {
        when(userService.findUser(any())).thenThrow(new NotFoundException());

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authRequest)
                .when()
                .post(LOG_IN_ENDPOINT)
                .then()
                .statusCode(404);
    }

    @Test
    void test_signup() {
        when(userService.findUser(any())).thenThrow(new NotFoundException());

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authRequest)
                .when()
                .post(SIGN_UP_ENDPOINT)
                .then()
                .statusCode(201);
    }

    @Test
    void test_signup_user_already_exists() {
        User user = new User(username, password);

        when(userService.findUser(any())).thenReturn(user);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authRequest)
                .when()
                .post(SIGN_UP_ENDPOINT)
                .then()
                .statusCode(409);
    }
}

package br.com.restassured;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.request;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OlaMundoTest {
    Response response = request(Method.GET, "https://restapi.wcaquino.me/ola");

    @Test
    public void testOlaMundo() {
        assertEquals(
                "Ola Mundo!",
                response.getBody().asString()
        );
    }

    @Test
    public void testOlaMundoBoolean() {
        assertTrue(
                response.getBody().asString().equals("Ola Mundo!")
        );

    }

    @Test
    public void statusCode() {
        assertEquals(
                200,
                response.getStatusCode());
    }

    @Test
    public void statusCodeBoolean() {
        assertTrue(
                response.getStatusCode() == 200
        );
    }

    // Outras formas de realizar testes com RestAssured

    /**
     * Como o get é um método estático, basta apertar alt + enter e tornar o método estático.
     * A forma abaixo é a representação do código:
     * Response response = request(Method.GET, "https://restapi.wcaquino.me/ola");
     * ValidatableResponse validação = response.then();
     * validação.statusCode(201);
     */
    @Test
    public void outrasFormasDeTestarRestAssured() {
        get("https://restapi.wcaquino.me/ola").then().statusCode(200);
    }

    //Modo Fluente
    @Test
    public void testeEmFormaDeGherkin() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/ola")
        .then()
//            .assertThat()
            .statusCode(200);
    }

    @Test
    public void devoValidarBody() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/ola")
        .then()
//            .assertThat() -> Hamcrest -> Matchers (deprecated)
            .statusCode(200)
            .body(is("Ola Mundo!"))
            .body(containsString("Mundo"))
            .body(is(not(nullValue())));
    }
}

/*FALHA x ERRO (Junit)
 * Falha-> Lançada excessão do tipo assertion error (verificação que não passou)
 * Erro-> throw new Runtime exception();*/

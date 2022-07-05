package br.com.restassured;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTest {

    @Test
    @DisplayName("Acessando API pública")
    public void deveAcessarSWAPI() {
        given()
                .log().all()
        .when()
                .get("https://swapi.dev/api/people/1/")
        .then()
                .log().all()
                .statusCode(200)
                .body("name", is("Luke Skywalker"))
                .body("height", is("172"))
        ;
    }

    //c91b58a385fe0e7389de815449aa082e
    //https://api.openweathermap.org/data/2.5/weather?q=Recife,BR&appid=c91b58a385fe0e7389de815449aa082e&units=metric

    @Test
    @DisplayName("Acessando API com chave")
    public void deveObterClima() {
        given()
                .log().all()
                .queryParam("q", "Recife,BR")
                .queryParam("appid", "c91b58a385fe0e7389de815449aa082e")
                .queryParam("units", "metric")
        .when()
                .get("https://api.openweathermap.org/data/2.5/weather")
        .then()
                .log().all()
                .statusCode(200)
                .body("name", is("Recife"))
                .body("coord.lon", is(-34.8811F))
                .body("main.temp", greaterThan(25.02F))
        ;
    }

    //https://restapi.wcaquino.me/basicauth
    //User: admin
    //senha: senha
    @Test
    public void nãoDeveAcessarSemSenha() {
        given()
                .log().all()
        .when()
                .get("https://restapi.wcaquino.me/basicauth")
        .then()
                .log().all()
                .statusCode(401) //Usuário não autenticado
        ;
    }

    @Test
    public void deveFazeAutenticacaoBasica() {
        given()
                .log().all()
        .when()
                .get("https://restapi.wcaquino.me/basicauth")
        .then()
                .log().all()
                .statusCode(401) //Usuário não autenticado
        ;
    }
}

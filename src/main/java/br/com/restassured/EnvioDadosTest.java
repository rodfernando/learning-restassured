package br.com.restassured;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.*;


public class EnvioDadosTest {

    @Test
    @DisplayName("Query")
    public void deveEnviarValorViaQuery() {
        given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/v2/users?format=xml")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML) //Content-Type: text/xml
        ;
    }

    @Test
    @DisplayName("Query via parâmetro")
    public void deveEnviarValorViaQueryViaParam() {
        given()
                .log().all()
                .queryParam("format", "xml") //Query params:	format=xml
                .queryParam("outro", "param")
                .when()
                .get("https://restapi.wcaquino.me/v2/users")
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML) //Content-Type: text/xml
                .contentType(Matchers.containsString("utf-8"))
        ;
    }

    @Test
    @DisplayName("Via Cabeçalho") // Accept=*/* -> aceita qualquer formato
    public void deveEnviarValorViaHEADER() {
        given()
                .log().all()
                .accept(ContentType.XML)//serve para dizer o tipo de resposta desejado
            .when()
                .get("https://restapi.wcaquino.me/v2/users") //Quando não envia nenhuma formato, o padrão é o HTML
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.XML) //Content-Type: text/xml
        ;
    }
}

package br.com.restassured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class HTML {

    @Test
    public void deveFazerBuscasComHTML() {
        given()
                .log().all()
        .when()
                .get("https://restapi.wcaquino.me/v2/users")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body("html.body.div.table.tbody.tr.size()", is(3)) //Verificação da quantidade de linhas da tabela
                .body("html.body.div.table.tbody.tr[1].td[2]", is("25"))//Dica: achar o caminho através do modo dev no browser
                .appendRootPath("html.body.div.table.tbody")
                .body("tr.find{it.toString().startsWith('2')}.td[1]", is("Maria Joaquina"))

        ;
    }
}

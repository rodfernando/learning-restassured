package br.com.restassured;


import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import io.restassured.matcher.RestAssuredMatchers;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

import static io.restassured.RestAssured.*;

public class SchemaTest {

    @Test
    public void deveValidarSchemaXML() {
        given()
                .log().all()
        .when()
                .get("https://restapi.wcaquino.me/usersXML")
        .then()
                .log().all()
                .statusCode(200)
                .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
        /*Até então o arquivo não é validado completamente, apesar de passar no teste.
        * Para isso, faz-se necessário verificar o xsd e realizar asserções
        * No exemplo https://restapi.wcaquino.me/InvalidUsersXML, falta o nome do do usuário
        * É importante verificar no xsd os atributos que são necessários e os opcionais(minOccurr)*/
        ;
    }

//    @Test(expected=SAXParseException.class)
//    public void deveInvalidarSchemaXML() {
//        given()
//                .log().all()
//        .when()
//                .get("https://restapi.wcaquino.me/InvalidUsersXML")
//        .then()
//                .log().all()
//                .statusCode(200)
//                .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
//        ;
//    }

//    @Test
//    public void deveValidarSchemaJson() {
//        given()
//                .log().all()
//        .when()
//                .get("https://restapi.wcaquino.me/users")
//        .then()
//                .log().all()
//                .statusCode(200)
//                //A Validação padrão para Json Schema não tem no RestAssured. Tem que baixar:
//                .body(JsonSchema)
//        //
//        ;
//    }
}

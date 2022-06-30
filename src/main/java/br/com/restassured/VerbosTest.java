package br.com.restassured;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VerbosTest {

    @Test
    public void deveSalvarUsuário() {
        given()
            .log().all()
                //tem que informar na requisição que o formato enviado é Json
            .contentType("application/json")
            .body("{\"name\": \"Jose\", \"age\": 50}") //objeto Json
        .when()
            .post("https://restapi.wcaquino.me/users")// do tipo post
        .then()
            .log().all()
            .statusCode(201) //Created
            .body("id", is(notNullValue()))
            .body("name", is("Jose"))
            .body("age", is(50))
        ;
    }

    @Test
    public void nãoDeveSalvarUsuarioSemNome() {
        given()
            .log().all()
            .contentType("application/json")
            .body("{\"age\": 50}")
        .when()
            .post("https://restapi.wcaquino.me/users\n")
        .then()
            .log().all()
            .statusCode(400) //Bad request
            .body("id", is(nullValue()))
            .body("name", is(nullValue()))
            .body("age", is(nullValue())) //apesar de haver idade, o valor se torna nulo por não ter um usuário
            .body("error" , is("Name é um atributo obrigatório"))
        ;
    }

    @Test
    public void deveSalvarUsuárioViaXML() {
        given()
                .log().all()
                //tem que informar na requisição que o formato enviado é Json
                .contentType(ContentType.XML)
                .body("<user><name>Jose</name><age>50</age></user>") //objeto Json
        .when()
                .post("https://restapi.wcaquino.me/usersXML")// do tipo post
        .then()
                .log().all()
                .statusCode(201) //Created
                .body("user.@id", is(notNullValue()))
                .body("user.name", is("Jose"))
                .body("user.age", is("50"))
        ;
    }

    @Test
    public void deveAtualizarUsuário() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\": \"Ribamar\", \"age\": 100}")
        .when()
                .put("https://restapi.wcaquino.me/users/1") //para atualizar, é necessário indicar qual user da lista deseja -> /1
        .then()
                .log().all()
                .statusCode(200) //Sucesso
                .body("id", is(1))
                .body("name", is("Ribamar"))
                .body("age", is(100))
                .body("salary", is(1234.5678f))
        ;
    }

    @Test
    public void devoCustomizarURL() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\": \"Ribamar\", \"age\": 100}")
        .when()
                //parametrização:
                .put("https://restapi.wcaquino.me/{entidade}/{userId}", "users", "1") //para atualizar, é necessário indicar qual user da lista deseja -> /1
        .then()
                .log().all()
                .statusCode(200) //Sucesso
                .body("id", is(1))
                .body("name", is("Ribamar"))
                .body("age", is(100))
                .body("salary", is(1234.5678f))
        ;
    }

    @Test
    public void devoCustomizarURLParte2() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\": \"Ribamar\", \"age\": 100}")
                .pathParam("entidade", "users")
                .pathParam("userId", "1")
        .when()
                //parametrização:
                .put("https://restapi.wcaquino.me/{entidade}/{userId}") //para atualizar, é necessário indicar qual user da lista deseja -> /1
        .then()
                .log().all()
                .statusCode(200) //Sucesso
                .body("id", is(1))
                .body("name", is("Ribamar"))
                .body("age", is(100))
                .body("salary", is(1234.5678f))
        ;
    }

    @Test
    public void devoDeletarUsuario(){
        given()
                .log().all()
                .contentType("application/json")
        .when()
                .delete("https://restapi.wcaquino.me/users/1")
        .then()
                .log().all()
                .statusCode(204) //sem conteúdo
        ;
    }

    @Test
    public void nãoDeveRemoverUsuarioInexistente(){
        given()
                .log().all()
                .contentType("application/json")
        .when()
                .delete("https://restapi.wcaquino.me/users/1000")
        .then()
                .log().all()
                .statusCode(400) //Bad Request
                .body("error", is("Registro inexistente"))
        ;
    }

//    https://restapi.wcaquino.me/users/1
}
//Anotação em Json:
// {"name": "Jose", "age": 50}

/*Anotação em XML:
como estou enviando, ainda não tem id
<user> <name>Jonatércio</name> <age>81</age> </user>*/


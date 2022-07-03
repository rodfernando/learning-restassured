package br.com.restassured;

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SerializandoMapVerbosTest {

    /**
     * Serializar um texto em objeto
     * Map é como se fosse uma lista, mas ele armazena pares.
     * É necessário baixar a biblioteca chamada "gson" para poder serializar o json na class.
     * Após isso, pode rodar novamente o test que irá passar
     */
    @Test
    @DisplayName("Serializando Map")
    public void deveSalvarUsuárioUsandoMap() {
        //map -> Json
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Usuario via map");
        params.put("age", 25);

        given()
            .log().all()
            .contentType("application/json")
            .body(params)
        .when()
            .post("https://restapi.wcaquino.me/users")// do tipo post
        .then()
            .log().all()
            .statusCode(201) //Created
            .body("id", is(notNullValue()))
            .body("name", is("Usuario via map"))
            .body("age", is(25))
        ;
    }

    @Test
    @DisplayName("Serializando Objeto")
    public void deveSalvarUsuárioUsandoObjeto() {
        //Objeto -> Json
        User user = new User("Usuario via objeto", 35);

        given()
                .log().all()
                .contentType("application/json")
                .body(user)
        .when()
                .post("https://restapi.wcaquino.me/users")
        .then()
                .log().all()
                .statusCode(201) //Created
                .body("id", is(notNullValue()))
                .body("name", is("Usuario via objeto"))
                .body("age", is(35))
        ;
    }

    @Test
    @DisplayName("Deserializando Objeto")
    public void deveDeserializarObjetoAoSalvarusuário() {
        //Objeto -> Json
        User user = new User("Usuario deserializado", 35);

        User usuarioInserido = given()
                .log().all()
                .contentType("application/json")
                .body(user)
            .when()
                .post("https://restapi.wcaquino.me/users")
            .then()
                .log().all()
                .statusCode(201) //Created
                .extract().body().as(User.class)//Como foi pedido a extração, alguma variável tem que receber. Logo, tem que declarar a variável
                ;
        System.out.println(usuarioInserido);
        Assert.assertEquals("Usuario deserializado", usuarioInserido.getName());
        Assert.assertThat(usuarioInserido.getAge(), is(35));
    }


    @Test
    @DisplayName("Serializando XML")
    public void deveSalvarUsuárioViaXMLUsandoObjeto() {
        //É necessário colocar a anotação em XML na classe e criar um construtor vazio
        User user = new User("Usuario XML", 40);
        given()
                .log().all()
                //tem que informar na requisição que o formato enviado é Json
                .contentType(ContentType.XML)
                .body(user)
        .when()
                .post("https://restapi.wcaquino.me/usersXML")// do tipo post
        .then()
                .log().all()
                .statusCode(201) //Created
                .body("user.@id", is(notNullValue()))
                .body("user.name", is("Usuario XML"))
                .body("user.age", is("40"))
        ;
    }

    @Test
    @DisplayName("Deserializando XML")
    public void deveDeserializarXMLAoSalvarUsuario() {
        User user = new User("Usuario XML", 40);
        User usuarioInseridoXML = given()
                .log().all()
                //tem que informar na requisição que o formato enviado é Json
                .contentType(ContentType.XML) //Para saber se foi convertido o XML para a Classe, basta ver no resultado: Content-Type: text/xml
                .body(user)
            .when()
                .post("https://restapi.wcaquino.me/usersXML")// do tipo post
            .then()
                .log().all()
                .statusCode(201) //Created
                .extract().body().as(User.class)
            ;
            Assert.assertThat(usuarioInseridoXML.getId(), notNullValue());
            Assert.assertThat(usuarioInseridoXML.getName(), is("Usuario XML"));
            Assert.assertThat(usuarioInseridoXML.getAge(), is(40));
        /*Se um atributo estiver dentro de uma tag, como exemplo o id, ele retornará um valor nulo
        * Faz-se necessário anotar na classe @XmlAttribute*/
            Assert.assertThat(usuarioInseridoXML.getSalary(), is(nullValue()));
    }
}



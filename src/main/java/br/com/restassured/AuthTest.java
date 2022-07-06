package br.com.restassured;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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

    //https://restapi.wcaquino.me/basicauth
    //User: admin
    //senha: senha
    //admin:senha@
    @Test
    public void deveFazeAutenticacaoBasica() {
        given()
                .log().all()
        .when()
                .get("https://admin:senha@restapi.wcaquino.me/basicauth")
        .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazeAutenticacaoBasica2() {
        given()
                .log().all()
                //forma de adicionar login e senha
                .auth().basic("admin", "senha")
        .when()
                .get("https://restapi.wcaquino.me/basicauth")
        .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazeAutenticacaoBasicaChallenge() {
        given()
                .log().all()
                //forma de adicionar login e senha
                .auth().preemptive().basic("admin", "senha")
        .when()
                .get("https://restapi.wcaquino.me/basicauth2")
        .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    /**- Vai fazer um login na APIRest https://barrigarest.wcaquino.me/contas
    *- Receber o token JWT
    *- Consulta nas contas cadastradas */
    @Test
    public void deveFazerAutenticaçãoComTokenJWT() {
        //passando o json para String usando map
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "rodrigo@oliveira");
        login.put("senha", "12345");

        //Login na api
        //Receber o token
        String token = given()
                .log().all()
                .body(login)
                .contentType(ContentType.JSON)
        .when()
                .post("https://barrigarest.wcaquino.me/signin")
        .then()
                .log().all()
                .statusCode(200)
                .extract().path("token");
        ;

        //Obter as contas
        given()
                .log().all()
                .header("Authorization", "JWT " + token)//envio do token JWT para a requisição
        .when()
                .get("https://barrigarest.wcaquino.me/contas")
        .then()
                .log().all()
                .statusCode(200) //dará erro 401, pois não foi enviado o token
                .body("nome", hasItems("Conta de teste"))//quando tem coleção de contas, usa hasItem
        ;
    }

    /*OBS: o token JWT além de garantir que apenas os usuários com acesso
    * enxergarão esses dados, ele também consegue fazer a divisão dos dados*/

    @Test
    public void deveAcessarAplicaçãoWeb() {
        String cookie = given()
                .log().all()
                //enviando dados de formulário
                .formParam("email", "rodrigo@oliveira")
                .formParam("senha", "12345")
                .contentType(ContentType.URLENC.withCharset("UTF-8"))
        .when()
                .post("https://seubarriga.wcaquino.me/logar")
        .then()
                .log().all()
                .statusCode(200)
                .extract().header("set-cookie");

        /*set-cookie: connect.sid=s%3A_nERsnPl_QtpNYOLQy08hs7YjvHePDIO.irkuYTnIg29%2BA66e%2BZLHOJViQp16apAFJoGjnpl%2F4VU; Path=/; HttpOnly
        * Este cookie é responsável por me manter conectado toda vez que entrar no site.
        * Deseja-se extrair a partir do sinal de = e ir até o ;
        * a posição [1] é o que está à direita do sinal, e [0] à esqueda.*/
        cookie = cookie.split("=")[1].split(";")[0];
        System.out.println(cookie);

        //Obter a conta
        String bodyConta = given()
                .log().all()
                .cookie("connect.sid", cookie)
                .when()
                .get("https://seubarriga.wcaquino.me/contas")
                .then()
                .log().all()
                .statusCode(200)
                .body("html.body.table.tbody.tr[0].td[0]", is("Conta de teste"))
                //Extraindo o nome dessa conta:
                .extract().body().asString()
                ;

        System.out.println("-".repeat(40));
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, bodyConta);
        System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
    }


}

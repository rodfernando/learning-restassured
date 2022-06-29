package br.com.restassured;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserXMLTest {

    @Test
    public void devoTrabalharComXML() {
        given()
            .when()
                .get("https://restapi.wcaquino.me/usersXML/3")
            .then()
                .statusCode(200)
                .body("user.name", is("Ana Julia"))
        //A diferença com o Json é que o XML possui atributos
                .body("user.@id", is("3")) //XMLtodo valor é string
                .body("user.filhos.name.size()", is(2))
                .body("user.filhos.name[0]", is("Zezinho"))
                //Testando coleção
                .body("user.filhos.name", hasItem("Zezinho"))
                .body("user.filhos.name", hasItems("Zezinho", "Luizinho"))
        ;
    }

    @Test
    public void devoTrabalharComXMLRootPath() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/usersXML/3")
        .then()
            .statusCode(200)
            .rootPath("user")
            .body("name", is("Ana Julia"))
            //A diferença com o Json é que o XML possui atributos
            .body("@id", is("3")) //XMLtodo valor é string

            //Nó de navegação
            .rootPath("user.filhos")
            .body("name.size()", is(2))

            //retirar do rootPath o valor (tem que declarar os valores)
            .detachRootPath("filhos")
            .body("filhos.name[0]", is("Zezinho"))
            .body("filhos.name", hasItem("Zezinho"))
            .body("filhos.name", hasItems("Zezinho", "Luizinho"))

            .appendRootPath("filhos")
            .body("name", hasItem("Zezinho"))
            .body("name", hasItems("Zezinho", "Luizinho"))
        ;
    }

    @Test
    public void devoFazerPesquisasAvançadasComXML(){
        given()
        .when()
            .get("https://restapi.wcaquino.me/usersXML")
        .then()
            .statusCode(200)
            .body("users.user.size()", is(3))
                //por ler "String", tem-se que transformar para int através do toInteger
            .body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
            .body("users.user.@id", hasItems("1", "2", "3"))
            .body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
                //Usando o método contains, tem que explicitar que quer uma String(toString()):
            .body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
            //Salário encontrado através de String
            .body("users.user.salary.find{it != null}", is("1234.5678"))
            //Salário encontrado através de Double:
            .body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
            //Multiplicação por 2 nas idades (it referencia as idades, ou seja, tem que tratar como número(toInteger()):
            .body("users.user.age.collect{it.toInteger() * 2}", hasItems(60, 50, 40))
            .body("users.user.name.findAll{it.toString().startsWith('Maria')}" + //isso me traz um conjunto de valores
                    ".collect{it.toString().toUpperCase()}", //transformação em cima do conjunto -> collect
                    is("MARIA JOAQUINA"))
        ;
    }

}

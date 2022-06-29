package br.com.restassured;

import io.restassured.RestAssured;
import io.restassured.internal.path.xml.NodeImpl;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
    public void devoFazerPesquisasAvançadasComXML() {
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

    //UNINDO XMLPath com Java

    /**
     * Dica: quando quiser imprimir o retorno de um assert, é bom criar uma variável e
     * imprimir usando sout
     */
    @Test
    public void devoFazerPesquisasAvançadasComXMLComJava() {
        Object nome = given()
                .when()
                .get("https://restapi.wcaquino.me/usersXML")
                .then()
                .statusCode(200)
                .extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}")
                //atalho para pegar o retorno dessa expressão e colocar numa variável: alt + enter -> Object path
                ;
        System.out.println(nome.toString());
        Assert.assertEquals("Maria Joaquina".toUpperCase(), nome.toString().toUpperCase());
    }


    /*
     * Nodeimpl ~ String. Em XML só funciona o Nodeimpl. Alguns métodos não funcionarão nesse formato,
     * precisando transformar-los (cast) em String ou qualquer outro tipo de atributo.
     * Dica: sempre bom colocar uma consulta grande dividida em partes. Uma boa forma é utilizando
     * a extração e realizando assertions
     */
    @Test
    public void devoFazerPesquisasAvançadasComXMLComJavaColeção() {
        ArrayList<NodeImpl> nomes = given()
                .when()
                .get("https://restapi.wcaquino.me/usersXML")
                .then()
                .statusCode(200)
                .extract().path("users.user.name.findAll{it.toString().contains('n')}");
        //Quando é retornado uma coleção(Array), mudamos o Object para ArrayList
        System.out.println("Coleção: " + nomes);
        Assert.assertEquals(2, nomes.size());
        Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
        Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString())); //sempre verificar se o método retorna um booleano, string ou int
    }

//Utilizando o Xpath
    @Test
    public void devoFazerPesquisasAvançadasComXPath() {
            given()
            .when()
                .get("https://restapi.wcaquino.me/usersXML")
            .then()
                .statusCode(200)
                //count() -> método do xpath
                .body(hasXPath("count(/users/user)", is("3")))
                //busca através do atributo @id
                .body(hasXPath("/users/user[@id = '1']"))
                //o "//" serve para navegar entre os objetos de forma direta.
                .body(hasXPath("//user[age = '30']"))
                //ex: descobrir quem é a mãe de Zezinho:
                .body(hasXPath("//name[text() = 'Luizinho']" + //caminho para chegar no nome Luizinho
                        "/../../name", is("Ana Julia")))  //Utiliza-se navegação de terminal para subir a pasta
                /*Descobrir o nome dos filhos a partir da mãe:
                following-sibling -> permite navegar entre os irmãos do campo em questão
                Checar site The Rosetta Stone*/

                .body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos",
                        allOf(containsString("Zezinho"), containsString("Luizinho"))))
                .body(hasXPath("//name", is("João da Silva")))
                .body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
                //Método que leva até o último registro da lista:
                .body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
            ;
    }
}

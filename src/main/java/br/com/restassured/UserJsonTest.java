package br.com.restassured;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserJsonTest {

    @Test
    public void deveVerificarPrimeiroNivel() {
        given()
        .when()
            //ação
            .get("https://restapi.wcaquino.me/users/1")
        .then()
            .statusCode(200)
            .body("id", is(1))
            .body("name", is("João da Silva"))
            .body("name", containsString("Silva"))
            .body("age", greaterThan(18))
//            .body("salary", is(1234.5678))
        ;
    }

    /**
     * retirar os dados do Json através do response e colocar no Junit
     */
    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas() {
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/users/1");

        //path
        int res = response.path("id"); // response.path("id") é um objeto
        System.out.println("response value: " + res);
        Assert.assertEquals(1, res);

        //jsonpath
        JsonPath jpath= new JsonPath(response.asString());
        Assert.assertEquals(1, jpath.getInt("id"));

        //from -> método estático do próprio JsonPath
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1 , id);
    }


    //Testando níveis
    @Test
    public void deveVerificarSegundoNivel() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/2")
        .then()
            .body("name", is("Maria Joaquina"))
            //testando Segundo Nível:
            .body("endereco.rua", is("Rua dos bobos"))
        // Se o objeto retornar null, é bom verificar se estes estão escritos corretamente
        ;
        }

    @Test
    public void deveVerificarObjetoSegundoNivel() {
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/users/2");

        String rua = response.path("endereco.rua");
        System.out.println("A rua é " + rua);
        int num = response.path("endereco.numero");
        System.out.println("O número é " + num);
//        JsonPath jpath= new JsonPath(response.asString());
//        Assert.assertEquals(0, jpath.getInt("endereco.numero"));
        }


    //Testando Listas
    @Test
    public void deveVerificarLista() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/3")
        .then()
            .statusCode(200)
            .body("name", containsString("Ana"))
            .body("filhos", hasSize(2))
            .body("filhos[0].name", is("Zezinho"))
            .body("filhos[1].name", is("Luizinho"))
            .body("filhos.name", hasItem("Zezinho"))
            .body("filhos.name", hasItems("Zezinho", "Luizinho"))
        ;
    }


    //Verificando erros
    @Test
    public void deveRetornarErroUsuarioInexistente() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/4")
        .then()
            .statusCode(404)
            .body("error", is("Usuário inexistente"))
        ;
    }


    //Lista completa (raiz)
    @Test
    public void deveVerificarListaRaiz() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users")
        .then()
            .statusCode(200)
            .body("$", hasSize(3)) //$ -> procurando na raíz
            //ou deixando vazio
            .body("", hasSize(3))
            //pegando o nome de todos da lista:
            .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
            //por index:
            .body("age[1]", is(25))
            //um array dentro de um array:
            .body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
            .body("salary", contains(1234.5678f, 2500, null))
        ;
    }

    @Test
    public void devoFazerVerificaçõesAvançadas() {
        //verificações utilizando o Groovy
        given()
        .when()
            .get("https://restapi.wcaquino.me/users")
        .then()
            .statusCode(200)
                //o objeto it será a instância do objeto age
            .body("age.findAll{it <= 25 && it > 20}.size()", is(1))
            .body("age.findAll{it <= 25 && it > 20}.size()", is(1))
                //nome da pessoa após o filtro. O is retorna uma String; o hasItem um objeto(lista)
            .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
                //transformar lista em objeto
            .body("findAll{it.age <= 25}[0].name", is(("Maria Joaquina")))
                //último nome da lista[-1]
            .body("findAll{it.age <= 25}[-1].name", is(("Ana Júlia")))
                //buscando apenas o primeiro elemento encontrado:
            .body("find{it.age <= 25}.name", is(("Maria Joaquina")))
                //Verificando se um nome contém uma string:
                //it vai buscar no findAll cada elemento referenciado e contido no objeto name e vai retornar name
            .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
            .body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))
                //Colocar os nomes em uppercase:
            .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                //Find + collect -> Buscar todos os nomes que começam com Maria e transformem ele em uppercase:
            .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
            .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", anyOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
            .body("age.collect{it * 2}", hasItems(60, 50, 40))
            .body("id.max()", is(3))
            .body("salary.min()", is(1234.5678f))
                //filtro para retirar o valor null e fazer somatoria
            .body("salary.findAll{it != null}.sum()", is(3734.5677490234375))
                //filtro com margem de erro
            .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
                //forma mais geral
            .body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))
        ;
    }

    /**
     * esta forma não utilizará o RestAssured via Hamcrest
     * Consulta feita utilizando java
     */
    @Test
    public void devoUnirJsonPathComJava() {
        ArrayList<String> names =
            given()
            .when()
                .get("https://restapi.wcaquino.me/users")
            .then()
                .statusCode(200)
                    .extract().path("name.findAll{it.startsWith('Maria')}")
                    //essa extração retorna uma lista de String
            ;
        Assert.assertEquals(1, names.size());
        //Nessa coleção quero o primeiro registro, pegando um equals ignorando o sensitive case
        Assert.assertTrue(names.get(0).equalsIgnoreCase("MaRiA JoAquinA"));
        Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
    }

}

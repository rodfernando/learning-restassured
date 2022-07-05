package br.com.restassured;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class FileTest {

    @Test
    public void deveObrigarEnviarArquivo() {
        given()
                .log().all()
        .when()
                .post("http://restapi.wcaquino.me/upload")
        .then()
                .log().all()
                .statusCode(404) //deveria ser 400
                .body("error", is("Arquivo não enviado"))
        ;
    }

    @Test
    public void deveFazerUploadArquivo() {
        given()
                .log().all()
                //enviando arquivo:
                .multiPart("arquivo", new File("src/main/resources/RestAPI.pdf")) //nome do parâmetro que vou enviar
        .when()
                .post("http://restapi.wcaquino.me/upload")
        .then()
                .log().all()
                .statusCode(200) //deveria ser 400
                .body("name", is("RestAPI.pdf"))
        ;
    }

    @Test
    public void nãoDeveFazerUploadArquivoGrande() {
        given()
                .log().all()
                //enviando arquivo:
                .multiPart("arquivo", new File("src/main/resources/Rosetta Stone.pdf")) //nome do parâmetro que vou enviar
        .when()
                .post("http://restapi.wcaquino.me/upload")
        .then()
                .log().all()
                //Limitando o tempo de resposta da requisição:
                .time(lessThan(2000L)) //2 segundos + Long
                .statusCode(413) //deveria ser 400
        ;
    }

    @Test
    public void deveBaixarArquivo() throws IOException {
        byte[] photo = given()
                .log().all()
        .when()
                .get("http://restapi.wcaquino.me/download")//para enviar um arquivo:post; para receber: get
        .then()
                .statusCode(200)
                .extract().asByteArray(); //extrair como um conjunto de Byte (foto)
        ;

        //Definindo caminho do download do arquivo:
        File imagem = new File("src/main/resources/file.jpg");
        //Output que vai escrever o array de byte dentro da imagem:
        OutputStream out= new FileOutputStream(imagem); //lembrar de adicionar a exceção
        out.write(photo);
        out.close();

//        System.out.println(imagem.length());
        //Verificando tamanho do arquivo:
        Assert.assertThat(imagem.length(), lessThan(10000L));
    }
}

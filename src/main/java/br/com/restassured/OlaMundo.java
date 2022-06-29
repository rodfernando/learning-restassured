package br.com.restassured;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundo {
    public static void main(String[] args) {
        String url = "https://restapi.wcaquino.me/ola";
        // shortcut to Assign statement to a new local = alt + enter

        /*RestAssured.request(
                Method.GET, "https://restapi.wcaquino.me/ola"
        );*/

        /**
         * o response entregará o corpo da msg através do método get
         */
        Response response = RestAssured.request(Method.GET, url);
        System.out.println("A mensagem do corpo é '" + response.getBody().asString() + "'.");
        System.out.println(response.getBody().asString().equals("Ola Mundo!"));
        System.out.println("Status code: " + response.getStatusCode());

        /**
         * A partir da verificação, pode-se validar através do comando .then
         * response.then(); -> alt + enter
         */
        ValidatableResponse validação = response.then();
        validação.statusCode(201);

    }

    //uma forma de mapear a porta é digitando no terminal ping restapi.wcaquino.me
}

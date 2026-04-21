package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.apiSitac.repositories.AdMailsConfigRepository;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

    public void send(String jsonBody, AdMailConfigEntity adConfigMailEntity) {

        String url = "https://api.turbo-smtp.com/api/v2/mail/send";
        String consumerkey = adConfigMailEntity.getConsumerKey();
        String consumersecret = adConfigMailEntity.getConsumerSecret();

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Consumerkey", consumerkey)
                .header("Consumersecret", consumersecret)
                //.header("Authorization", "Bearer " + this.tmdbApiToken)
                .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request1, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String args[]) {
        pruebaEnviar();
    }

    public static void pruebaEnviar() {
        System.out.println("Hola");
        String url = "";
        String consumerKey = "";
        String consumerSecret = "";
        String jsonBody = "{   \n" +
                "    \"from\":\"noresponder@software.com.ec\",\n" +
                "    \"to\":\"sitacsoftware@gmail.com\",\n" +
                "    \"subject\":\"contenido\"\n" +
                "}";
        //send(url, consumerKey, consumerSecret, jsonBody);
    }


}




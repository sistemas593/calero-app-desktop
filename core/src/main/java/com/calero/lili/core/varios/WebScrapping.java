package com.calero.lili.core.varios;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebScrapping {
//    Si el servidor requiere autenticación u otros parámetros, deberás agregarlos al encabezado o URL.
    public static void main(String[] args) {
        // URL extraída del archivo .har
        String url = "https://srienlinea.sri.gob.ec/comprobantes-electronicos-internet/pages/consultas/recibidos/comprobantesRecibidos.jsf?&contextoMPT=https://srienlinea.sri.gob.ec/tuportal-internet&pathMPT=Facturaci%F3n%20Electr%F3nica&actualMPT=Comprobantes%20electr%F3nicos%20recibidos%20&linkMPT=%2Fcomprobantes-electronicos-internet%2Fpages%2Fconsultas%2Frecibidos%2FcomprobantesRecibidos.jsf%3F&esFavorito=S";

        // Crear el cliente HTTP
        HttpClient client = HttpClient.newHttpClient();

        // Construir la solicitud HTTP con los encabezados
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36")
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "es-ES,es;q=0.9")
                .header("Cache-Control", "max-age=0")
                .header("Connection", "keep-alive")
                .header("Host", "srienlinea.sri.gob.ec")
                .header("Upgrade-Insecure-Requests", "1")
                .build();

        // Enviar la solicitud y manejar la respuesta
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Imprimir el código de estado y el cuerpo de la respuesta
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Respuesta: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

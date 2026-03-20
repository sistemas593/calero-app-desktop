package com.calero.lili.core.comprobantesWs.services;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

@Service
@RequiredArgsConstructor

public class ProcesarFirmarXmlServiceImpl {

            // 1. Lo defines como una constante de la clase
            private static final HttpClient CLIENTE_HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

        public JsonNode firmarXml(String comprobante) {

            System.out.println("Firmando documento");
            HttpResponse<String> responseFirma = null;

            String urlFirma = "https://sgn.facturador.com.ec/api/v1.0/firmar";

                        String jsonBodyFirma = "{"
                    + "\"xmlFile\":\"" + Base64.getEncoder().encodeToString(comprobante.getBytes()) + "\","
                    + "\"certificadoDigital\":\"data00001/file001.p12\","
                    + "\"idData\":\"1\","
                                + "\"idEmpresa\":\"1\""

                                + "}";

            HttpRequest requestFirma = HttpRequest.newBuilder()
                .uri(URI.create(urlFirma))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(jsonBodyFirma))
                .build();

            try {
                responseFirma = CLIENTE_HTTP.send(requestFirma, HttpResponse.BodyHandlers.ofString());

                // 1. Obtener el código de estado
                int statusCode = responseFirma.statusCode();

                // 2. Validar si la respuesta NO es exitosa (fuera del rango 200-299)
                if (statusCode < 200 || statusCode >= 300) {
                    String errorBody = responseFirma.body(); // El API suele enviar el motivo del error aquí

                    if (statusCode >= 400 && statusCode < 500) {
                        // Error del cliente (Ej: JSON mal formado, certificado no encontrado)
                        throw new GeneralException("Error en la solicitud (4xx): " + errorBody);
                    } else {
                        // Error del servidor (Ej: El firmador se cayó)
                        throw new GeneralException("Error en el servidor de firma (5xx): " + errorBody);
                    }
                }

                // Si llega aquí, el status es 200 OK
                String resultado = responseFirma.body();

            } catch (IOException | InterruptedException e) {
                // Errores de conectividad, timeout o interrupción
                Thread.currentThread().interrupt(); // Buena práctica si es InterruptedException
                throw new GeneralException("Error de conexión al intentar firmar: " + e.getMessage());
            }

        System.out.println("firmado");

        // MAPEAR DE JSON A OBJETO Y VICEVERSA
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(responseFirma.body());
            System.out.println(responseFirma.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if(jsonNode.get("xmlFirmado") == null){
            throw new GeneralException("Error al recuperar el xml Firmado");
        }

        System.out.println("xmlFirmado correctamente");

        return jsonNode;
    }




}

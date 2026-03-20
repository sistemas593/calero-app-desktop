package com.calero.lili.core.modAdminlistaNegra;

import com.calero.lili.core.apiSitac.repositories.AdMailsConfigRepository;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import com.calero.lili.core.modAdminlistaNegra.dto.MailsBlackDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class EmailProcesarRechazados {

    private final AdMailsListaNegraRepository adMailsListaNegraRepository;
    private final AdMailsListaNegraBuilder adMailsListaNegraBuilder;
    private final AdMailsConfigRepository adMailsConfigRepository;


    public void procesarRechazados() {
        System.out.println("Procesar rechazados");

        LocalDate dateNow = LocalDate.now();
        String fechaFin = dateNow.toString();
        String fechaInicio = dateNow.minusDays(2).toString();

        String url = "https://pro.api.serversmtp.com/api/v2/analytics?page=1&limit=100&from=" + fechaInicio + "&to=" + fechaFin + "&status[]=SYSFAIL&status[]=FAIL&status[]=REPORT";

        AdMailConfigEntity adConfigMailEntity = adMailsConfigRepository.findByIdConfig(Long.valueOf(1));

        // Specify Credentials
        String Consumerkey = adConfigMailEntity.getConsumerKey();
        String Consumersecret = adConfigMailEntity.getConsumerSecret();

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Consumerkey", Consumerkey)
                .header("Consumersecret", Consumersecret)
                //.header("Authorization", "Bearer " + this.tmdbApiToken)
                .method("GET", HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request1, HttpResponse.BodyHandlers.ofString());

            System.out.println(response);

            saveEmailBlackList(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void saveEmailBlackList(HttpResponse<String> response) {

        log.info("Iniciando proceso de guardado de correos en lista negra");
        if (response.statusCode() == 200) {
            List<MailsBlackDto> listaNegraResponse = validateJsonResults(response);
            if (Objects.nonNull(listaNegraResponse)) {

                List<String> lista = getRecipientEmails(listaNegraResponse);

                List<AdMailListaNegraEntity> listaNegraEntidad = adMailsListaNegraRepository
                        .findByEmails(lista);

                List<MailsBlackDto> nuevos = getNuevosCorreos(listaNegraEntidad, listaNegraResponse);

                adMailsListaNegraRepository.saveAll(adMailsListaNegraBuilder
                        .builderList(nuevos));

            }
        }

        log.info("Proceso de guardado de correos en lista negra finalizado");

    }

    private static List<MailsBlackDto> getNuevosCorreos(List<AdMailListaNegraEntity> listaNegraEntidad,
                                                        List<MailsBlackDto> listaNegraResponse) {

        Set<String> emailsGuardados = listaNegraEntidad.stream()
                .map(AdMailListaNegraEntity::getEmail)
                .collect(Collectors.toSet());

        return listaNegraResponse.stream()
                .filter(dto -> !emailsGuardados.contains(dto.getRecipient()))
                .collect(Collectors.toList());

    }

    private static List<String> getRecipientEmails(List<MailsBlackDto> listaNegraResponse) {
        return listaNegraResponse.stream()
                .map(MailsBlackDto::getRecipient)
                .collect(Collectors.toList());
    }

    private List<MailsBlackDto> validateJsonResults(HttpResponse<String> response) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.body());
            JsonNode results = json.get("results");

            if (Objects.nonNull(results) && results.isArray() && !results.isEmpty()) {
                return convertJsonToDtoList(results, mapper);
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new GeneralException("Error al procesar las respuestas: " + ex.getMessage());
        }
    }

    private List<MailsBlackDto> convertJsonToDtoList(JsonNode results, ObjectMapper mapper)
            throws JsonProcessingException {
        return mapper.readValue(
                results.toString(),
                new TypeReference<List<MailsBlackDto>>() {
                }
        );
    }

}




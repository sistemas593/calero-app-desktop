package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.apiSitac.dtos.EnvioCorreoModeloDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

    public String send(EnvioCorreoModeloDto dto) {

        Resend resend = new Resend(dto.getTokenApi());

        CreateEmailOptions.Builder builder = CreateEmailOptions.builder()
                .from(dto.getEmailFrom())
                .to(dto.getEmailTo())
                .subject(dto.getSubject())
                .html(dto.getBody());

        if (Objects.nonNull(dto.getPdf())) {

            Attachment pdf = Attachment.builder()
                    .fileName(dto.getInicialesDocumento() + "-" + dto.getClaveAcceso() + ".pdf")
                    .content(dto.getPdf())
                    .build();

            builder.addAttachment(pdf);
        }

        if (Objects.nonNull(dto.getXml())) {

            Attachment xml = Attachment.builder()
                    .fileName(dto.getInicialesDocumento() + "-" + dto.getClaveAcceso() + ".xml")
                    .content(dto.getXml())
                    .build();

            builder.addAttachment(xml);
        }

        CreateEmailOptions params = builder.build();

        try {
            CreateEmailResponse data = resend.emails().send(params);

            System.out.println(data.getId());

            return data.getId();

        } catch (ResendException e) {

            e.printStackTrace();
            throw new GeneralException("Error al enviar el correo");

        }


    }

    public static void main(String args[]) {
        pruebaEnviar();
    }

    public static void pruebaEnviar() {

        /* System.out.println("Hola");
        String url = "";
        String consumerKey = "";
        String consumerSecret = "";
        String jsonBody = "{   \n" +
                "    \"from\":\"noresponder@software.com.ec\",\n" +
                "    \"to\":\"sitacsoftware@gmail.com\",\n" +
                "    \"subject\":\"contenido\"\n" +
                "}";
        //send(url, consumerKey, consumerSecret, jsonBody);*/

        /*Resend resend = new Resend("");
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Acme <noresponder@software.com.ec>")
                .to("ismab2@hotmail.com")
                .subject("GENIAL")
                .html("<strong>Prueba</strong>")
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
    }*/



    /*String url = "https://api.turbo-smtp.com/api/v2/mail/send";
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
        HttpResponse<String> response = null;*/

    /*try {
            response = HttpClient.newHttpClient().send(request1, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/

    }

}



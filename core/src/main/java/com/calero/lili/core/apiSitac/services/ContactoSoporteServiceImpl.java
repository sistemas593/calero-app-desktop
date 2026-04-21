package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.apiSitac.dtos.contacto.ContactoRequestDto;
import com.calero.lili.core.apiSitac.repositories.AdMailsConfigRepository;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactoSoporteServiceImpl {

    private final AdMailsConfigRepository adConfigRepository;
    private final EmailSender emailSender;

    public void enviarContacto(ContactoRequestDto request) {
        AdMailConfigEntity config = adConfigRepository.findByIdConfig(Long.valueOf(1));

        String url = "https://api.turbo-smtp.com/api/v2/mail/send";
        String mailFrom = config.getEmailFrom();
        String consumerKey = config.getConsumerKey();
        String consumerSecret = config.getConsumerSecret();

        String jsonBody = "{\n" +
                "  \"from\":\"" + mailFrom + "\",\n" +
                "  \"to\":\"soporte@software.com.ec\",\n" +
                "  \"subject\":\"Nuevo contacto web: " + request.getName() + "\",\n" +
                "  \"html_content\":\"<h3>Nuevo mensaje de contacto</h3>" +
                "<p><b>Nombre:</b> " + request.getName() + "</p>" +
                "<p><b>Email:</b> " + request.getEmail() + "</p>" +
                "<p><b>Teléfono:</b> " + request.getNumber() + "</p>" +
                "<p><b>Mensaje:</b> " + request.getMessage() + "</p>\"\n" +
                "}";

        emailSender.send(jsonBody, config);
    }
}

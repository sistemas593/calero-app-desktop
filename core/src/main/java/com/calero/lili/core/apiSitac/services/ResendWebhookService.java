package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.apiSitac.dtos.webhook.ResendWebhookDataDto;
import com.calero.lili.core.apiSitac.dtos.webhook.ResendWebhookEventDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminlistaNegra.AdMailsListaNegraBuilder;
import com.calero.lili.core.modAdminlistaNegra.AdMailsListaNegraRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

/**
 * Procesa los eventos que Resend envía mediante webhook para informar qué ocurrió
 * con un correo enviado (entregado, rebotado, marcado como spam, fallido, etc.)
 * <p>
 * Ver tipos de eventos: https://resend.com/docs/dashboard/webhooks/event-types
 * <p>
 * Cuando el evento representa un problema con el envío (rebote, spam o fallo), se
 * guarda/actualiza el correo en la lista negra (AdMailListaNegraEntity) con el motivo
 * y la fecha del incidente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResendWebhookService {

    // Eventos de Resend que representan un problema con el envío del correo
    private static final Set<String> EVENTOS_CON_PROBLEMA = Set.of(
            "email.bounced",
            "email.complained",
            "email.failed"
    );

    private final ResendWebhookSignatureVerifier resendWebhookSignatureVerifier;
    private final AdMailsListaNegraRepository adMailsListaNegraRepository;
    private final AdMailsListaNegraBuilder adMailsListaNegraBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void procesarEvento(String payload, String svixId, String svixTimestamp, String svixSignature) {

       // log.info("Recibido webhook de Resend: svix-id={}, svix-timestamp={}, svix-signature={}", svixId, svixTimestamp, svixSignature);

        resendWebhookSignatureVerifier.verificar(payload, svixId, svixTimestamp, svixSignature);

        ResendWebhookEventDto evento = parsearEvento(payload);

        log.info("Evento de Resend recibido: type={}, email_id={}, svix-id={}",
                evento.getType(),
                Objects.nonNull(evento.getData()) ? evento.getData().getEmailId() : null,
                svixId);

        if (!EVENTOS_CON_PROBLEMA.contains(evento.getType())) {
            log.warn("Evento de Resend que representan un problema con el envío del correo:  {}", evento.getType());
            // email.sent, email.delivered, email.opened, email.clicked, etc. no representan un problema
            return;
        }

        registrarEnListaNegra(evento);
    }

    private ResendWebhookEventDto parsearEvento(String payload) {

        log.info("Convetir el payload de String a un objecto ResendWebhookEventDto");

        try {
            return objectMapper.readValue(payload, ResendWebhookEventDto.class);
        } catch (Exception e) {
            throw new GeneralException("No se pudo procesar el payload del webhook de Resend: " + e.getMessage());
        }
    }

    private void registrarEnListaNegra(ResendWebhookEventDto evento) {

        ResendWebhookDataDto data = evento.getData();

        if (Objects.isNull(data) || Objects.isNull(data.getTo()) || data.getTo().isEmpty()) {
            log.warn("Evento {} de Resend sin destinatarios (data.to), no se guarda en lista negra", evento.getType());
            return;
        }

        String motivo = construirMotivo(evento);

        data.getTo().forEach(correo -> {

            log.info("Inicia proceso de registro en lista negra en caso de ser necesario");
            String correoNormalizado = correo.trim();
            log.info("Correo guardar en lista negrea: {}", correoNormalizado);
            log.info("Motivo: {}", motivo);
            adMailsListaNegraRepository.save(adMailsListaNegraBuilder.builderDesdeWebhook(correoNormalizado));
            log.info("Correo {} agregado/actualizado en lista negra por evento {}", correoNormalizado, evento.getType());
        });
    }

    private String construirMotivo(ResendWebhookEventDto evento) {

        ResendWebhookDataDto data = evento.getData();

        return switch (evento.getType()) {
            case "email.bounced" -> Objects.nonNull(data.getBounce())
                    ? String.format("Rebote (%s/%s): %s",
                    data.getBounce().getType(), data.getBounce().getSubType(), data.getBounce().getMessage())
                    : "Rebote de correo (email.bounced)";
            case "email.complained" -> "El destinatario marcó el correo como spam (email.complained)";
            case "email.failed" -> Objects.nonNull(data.getFailed())
                    ? "Falló el envío del correo: " + data.getFailed().getReason()
                    : "Falló el envío del correo (email.failed)";
            default -> "Evento de Resend: " + evento.getType();
        };
    }

}

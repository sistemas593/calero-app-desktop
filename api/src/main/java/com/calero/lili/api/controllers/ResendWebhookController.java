package com.calero.lili.api.controllers;

import com.calero.lili.core.apiSitac.services.ResendWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recibe los eventos (webhooks) enviados por Resend para notificar qué ocurrió con
 * los correos enviados: entregado, rebotado, marcado como spam, fallido, etc.
 * <p>
 * Este endpoint debe registrarse en el panel de Resend: https://resend.com/webhooks
 * usando la URL pública, por ejemplo: https://tu-dominio.com/apist/v1.0/resend-webhook
 * <p>
 * IMPORTANTE: se recibe el cuerpo como String (@RequestBody String) y no como un DTO
 * ya deserializado, porque la verificación de la firma (svix-signature) debe hacerse
 * sobre el cuerpo crudo (raw) exactamente como fue enviado. Si Spring lo deserializa
 * y se vuelve a serializar, la firma dejaría de coincidir.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("apist/v1.0/resend-webhook")
@CrossOrigin(originPatterns = "*")
public class ResendWebhookController {

    private final ResendWebhookService resendWebhookService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void recibirEvento(@RequestBody String payload,
                               @RequestHeader(value = "svix-id", required = false) String svixId,
                               @RequestHeader(value = "svix-timestamp", required = false) String svixTimestamp,
                               @RequestHeader(value = "svix-signature", required = false) String svixSignature) {

        resendWebhookService.procesarEvento(payload, svixId, svixTimestamp, svixSignature);
    }

}

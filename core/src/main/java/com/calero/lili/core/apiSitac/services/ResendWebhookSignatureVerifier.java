package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * Verifica la firma de los webhooks enviados por Resend.
 * <p>
 * Resend firma sus webhooks siguiendo el estándar de Svix:
 * https://resend.com/docs/dashboard/webhooks/verify-webhooks-requests
 * <p>
 * Cabeceras recibidas en cada petición:
 * - svix-id: identificador único del evento (útil para evitar procesar duplicados)
 * - svix-timestamp: fecha/hora (epoch en segundos) en que se envió el evento
 * - svix-signature: una o varias firmas separadas por espacio, cada una con el formato "v1,<firma-base64>"
 * <p>
 * El secreto (resend.webhook.secret) se obtiene desde el panel de Resend al crear el webhook
 * y tiene el formato "whsec_...".
 */
@Component
@Slf4j
public class ResendWebhookSignatureVerifier {

    private static final String ALGORITMO_HMAC = "HmacSHA256";
    private static final String PREFIJO_SECRETO = "whsec_";
    private static final long TOLERANCIA_SEGUNDOS = 300; // 5 minutos, evita ataques de repetición (replay)

    @Value("${resend.webhook.secret}")
    private String webhookSecret;

    public void verificar(String payload, String svixId, String svixTimestamp, String svixSignature) {

        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new GeneralException("No se ha configurado el secreto del webhook de Resend (propiedad resend.webhook.secret)");
        }

        if (Objects.isNull(payload) || Objects.isNull(svixId) || Objects.isNull(svixTimestamp) || Objects.isNull(svixSignature)) {
            throw new GeneralException("Faltan datos para verificar el webhook de Resend (cabeceras svix-id, svix-timestamp, svix-signature)");
        }

        validarAntiguedad(svixTimestamp);

        String firmaEsperada = calcularFirma(svixId, svixTimestamp, payload);

        boolean firmaValida = extraerFirmas(svixSignature).stream()
                .anyMatch(firmaRecibida -> compararEnTiempoConstante(firmaEsperada, firmaRecibida));

        if (!firmaValida) {
            log.warn("Firma inválida recibida en webhook de Resend (svix-id={})", svixId);
            throw new GeneralException("La firma del webhook de Resend no es válida");
        }
    }

    private void validarAntiguedad(String svixTimestamp) {
        try {
            long timestampEvento = Long.parseLong(svixTimestamp);
            long ahora = Instant.now().getEpochSecond();
            if (Math.abs(ahora - timestampEvento) > TOLERANCIA_SEGUNDOS) {
                throw new GeneralException("El webhook de Resend fue recibido fuera del tiempo permitido");
            }
        } catch (NumberFormatException e) {
            throw new GeneralException("La cabecera svix-timestamp del webhook de Resend es inválida");
        }
    }

    private String calcularFirma(String svixId, String svixTimestamp, String payload) {
        try {
            String contenidoFirmado = svixId + "." + svixTimestamp + "." + payload;

            String secretoSinPrefijo = webhookSecret.startsWith(PREFIJO_SECRETO)
                    ? webhookSecret.substring(PREFIJO_SECRETO.length())
                    : webhookSecret;

            byte[] secretoBytes = Base64.getDecoder().decode(secretoSinPrefijo);

            Mac mac = Mac.getInstance(ALGORITMO_HMAC);
            mac.init(new SecretKeySpec(secretoBytes, ALGORITMO_HMAC));

            byte[] hash = mac.doFinal(contenidoFirmado.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException("Error al calcular la firma del webhook de Resend: " + e.getMessage());
        }
    }

    private List<String> extraerFirmas(String svixSignature) {
        return Arrays.stream(svixSignature.trim().split(" "))
                .map(token -> {
                    int indiceComa = token.indexOf(',');
                    return indiceComa >= 0 ? token.substring(indiceComa + 1) : token;
                })
                .toList();
    }

    private boolean compararEnTiempoConstante(String firmaEsperada, String firmaRecibida) {
        return MessageDigest.isEqual(
                firmaEsperada.getBytes(StandardCharsets.UTF_8),
                firmaRecibida.getBytes(StandardCharsets.UTF_8));
    }

}

package com.calero.lili.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventoResend {

    // Eventos de correo
    correoDevuelto("email.bounced", "Correo devuelto"),
    correoClickeado("email.clicked", "Enlace del correo clickeado"),
    correoMarcadoSpam("email.complained", "Correo marcado como spam"),
    correoEntregado("email.delivered", "Correo entregado"),
    correoEntregaRetrasada("email.delivery_delayed", "Entrega del correo retrasada"),
    correoFallido("email.failed", "Envío de correo fallido"),
    correoAbierto("email.opened", "Correo abierto"),
    correoRecibido("email.received", "Correo recibido"),
    correoProgramado("email.scheduled", "Correo programado"),
    correoEnviado("email.sent", "Correo enviado"),
    correoSuprimido("email.suppressed", "Correo suprimido"),

    // Eventos de dominio
    dominioCreado("domain.created", "Dominio creado"),
    dominioActualizado("domain.updated", "Dominio actualizado"),
    dominioEliminado("domain.deleted", "Dominio eliminado"),

    // Eventos de contacto
    contactoCreado("contact.created", "Contacto creado"),
    contactoActualizado("contact.updated", "Contacto actualizado"),
    contactoEliminado("contact.deleted", "Contacto eliminado"),

    // Eventos de supresión
    supresionAgregada("suppression.added", "Correo agregado a la lista de supresión"),
    supresionEliminada("suppression.removed", "Correo eliminado de la lista de supresión");

    private final String evento;
    private final String mensaje;

    public static EventoResend obtenerPorEvento(String evento) {
        for (EventoResend eventoResend : EventoResend.values()) {
            if (eventoResend.getEvento().equalsIgnoreCase(evento)) {
                return eventoResend;
            }
        }
        throw new IllegalArgumentException("Evento de Resend no válido: " + evento);
    }
}

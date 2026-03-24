package com.calero.lili.core.modClientesTickets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VtClientesTicketsGetDtoOne {

    private UUID idTicket;
    private String asunto;
    private String contacto;
    private String detalle;
    private String respaldo;
    private String fecha;
    private String estado;
}

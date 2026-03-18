package com.calero.lili.core.modClientesTickets.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VtClientesTicketsCreationRequestDto {

    private String asunto;
    private String contacto;
    private String detalle;
    private String respaldo;
    private String fecha;
    private String estado;
    private String username;
    private UUID idTercero;


}

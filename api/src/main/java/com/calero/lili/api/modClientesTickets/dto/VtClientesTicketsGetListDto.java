package com.calero.lili.api.modClientesTickets.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VtClientesTicketsGetListDto {

    private UUID idNovedad;
    private String asunto;
    private String contacto;
    private String detalle;
    private String respaldo;
    private String fecha;
    private String estado;

}

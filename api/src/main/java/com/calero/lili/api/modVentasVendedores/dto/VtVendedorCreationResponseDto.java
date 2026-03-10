package com.calero.lili.api.modVentasVendedores.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class VtVendedorCreationResponseDto {

    private UUID idVendedor;

    private String vendedor;

    private Boolean bloqueado;

    private String firma;
}

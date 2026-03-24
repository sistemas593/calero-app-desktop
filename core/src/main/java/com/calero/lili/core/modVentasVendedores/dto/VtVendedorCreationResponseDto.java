package com.calero.lili.core.modVentasVendedores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VtVendedorCreationResponseDto {

    private UUID idVendedor;

    private String vendedor;

    private Boolean bloqueado;

    private String firma;
}

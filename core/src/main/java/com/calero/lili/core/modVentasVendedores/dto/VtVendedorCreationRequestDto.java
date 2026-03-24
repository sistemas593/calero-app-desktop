package com.calero.lili.core.modVentasVendedores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VtVendedorCreationRequestDto {

    private String vendedor;

    private Boolean bloqueado;

    private String firma;
}

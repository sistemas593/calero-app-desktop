package com.calero.lili.api.modVentasVendedores.dto;

import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class VtVendedorCreationRequestDto {

    private String vendedor;

    private Boolean bloqueado;

    private String firma;
}

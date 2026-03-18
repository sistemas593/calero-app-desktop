package com.calero.lili.core.modComprasItemsMedidas.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class GeItemMedidaCreationResponseDto {
    private UUID idUnidadMedida;
    private String unidadMedida;

}

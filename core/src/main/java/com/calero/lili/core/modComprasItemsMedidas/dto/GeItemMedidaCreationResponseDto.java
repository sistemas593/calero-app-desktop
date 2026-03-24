package com.calero.lili.core.modComprasItemsMedidas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeItemMedidaCreationResponseDto {
    private UUID idUnidadMedida;
    private String unidadMedida;

}

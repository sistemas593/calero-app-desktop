package com.calero.lili.api.modComprasItemsMarcas.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class GeItemMedidaCreationResponseDto {
    private UUID idMarca;
    private String marca;

}

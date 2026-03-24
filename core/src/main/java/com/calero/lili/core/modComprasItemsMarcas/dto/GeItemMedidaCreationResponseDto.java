package com.calero.lili.core.modComprasItemsMarcas.dto;

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
    private UUID idMarca;
    private String marca;

}

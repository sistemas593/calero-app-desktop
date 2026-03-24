package com.calero.lili.core.modComprasItemsCategorias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeItemCategoriaCreationResponseDto {
    private UUID idCategoria;
    private String categoria;
    private String nivel;

}

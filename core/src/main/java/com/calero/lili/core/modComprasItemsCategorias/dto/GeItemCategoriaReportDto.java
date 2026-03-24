package com.calero.lili.core.modComprasItemsCategorias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeItemCategoriaReportDto {

    private UUID idCategoria;
    private String categoria;
    private String nivel;
}

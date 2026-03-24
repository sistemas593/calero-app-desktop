package com.calero.lili.core.modComprasItemsMarcas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeItemMarcasReportDto {

    private UUID idMarca;
    private String marca;
}

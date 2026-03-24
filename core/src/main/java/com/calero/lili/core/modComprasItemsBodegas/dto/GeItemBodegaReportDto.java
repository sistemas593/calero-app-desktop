package com.calero.lili.core.modComprasItemsBodegas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeItemBodegaReportDto {
    private UUID idBodega;
    private String bodega;
    private String sucursal;
}

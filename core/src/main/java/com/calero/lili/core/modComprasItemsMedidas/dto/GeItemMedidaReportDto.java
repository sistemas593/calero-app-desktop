package com.calero.lili.core.modComprasItemsMedidas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeItemMedidaReportDto {
    private UUID idUnidadMedida;
    private String unidadMedida;
}

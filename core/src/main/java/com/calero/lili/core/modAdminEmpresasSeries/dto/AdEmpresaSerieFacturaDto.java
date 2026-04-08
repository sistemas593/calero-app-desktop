package com.calero.lili.core.modAdminEmpresasSeries.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdEmpresaSerieFacturaDto {
    private UUID idSerie;
    private String serie;
    private String nombreComercial;
    private String secuencial;
}

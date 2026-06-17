package com.calero.lili.core.modVentas.reporteCredito.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatosCrediticiosResponseDto {

    private UUID idDatosCrediticios;
    private String periodo;

}
